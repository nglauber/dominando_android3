package dominando.android.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val btAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val remoteDevices = mutableListOf<BluetoothDevice>()
    private var btThread: BtThread? = null
    private var btEventsReceiver: BtEventsReceiver? = null
    private var messagesAdapter: ArrayAdapter<String>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        lstMessages.adapter = messagesAdapter
        if (btAdapter != null) {
            if (btAdapter.isEnabled) {
                checkLocationPermission()
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, BT_ACTIVATE)
            }
        } else {
            Toast.makeText(this, R.string.msg_error_bt_not_found,
                Toast.LENGTH_LONG).show()
            finish()
        }
        registerBluetoothEventReceiver()
        btnSend.setOnClickListener {
            sendButtonClick()
        }
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                RC_LOCATION_PERMISSION)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BT_ACTIVATE) {
            if (Activity.RESULT_OK == resultCode) {
                checkLocationPermission()
            } else {
                Toast.makeText(this, R.string.msg_activate_bluetooth,
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == BT_VISIBLE) {
            if (resultCode == BT_DISCOVERY_TIME) {
                startServerThread()
            } else {
                hideProgress()
                Toast.makeText(this, R.string.msg_device_invisible,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun registerBluetoothEventReceiver() {
        btEventsReceiver = BtEventsReceiver()
        val filter1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val filter2 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(btEventsReceiver, filter1)
        registerReceiver(btEventsReceiver, filter2)
    }
    override fun onDestroy() {
        unregisterBluetoothEventReceiver()
        stopAll()
        super.onDestroy()
    }
    private fun unregisterBluetoothEventReceiver() {
        unregisterReceiver(btEventsReceiver)
    }

    private fun stopAll() {
        btThread?.stopThread()
        btThread = null
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bluetooth_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_client -> startClient()
            R.id.action_server -> startServer()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun startServer() {
        val discoverableIntent = Intent(
            BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(
            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
            BT_DISCOVERY_TIME)
        startActivityForResult(discoverableIntent, BT_VISIBLE)
    }
    private fun startServerThread() {
        showProgress(R.string.msg_server, BT_DISCOVERY_TIME.toLong() * 1000, cancelClick = {
            stopAll()
        })
        stopAll()
        val uiHandler = UiHandler(this::onMessageReceived, this::onConnectionChanged)
        btThread = BtThreadServer(btAdapter, uiHandler)
        btThread?.startThread()
    }
    private fun startClient() {
        showProgress(R.string.msg_searching_server, BT_DISCOVERY_TIME * 1000L) {
            btAdapter?.cancelDiscovery()
            stopAll()
        }
        remoteDevices.clear()
        btAdapter?.startDiscovery()
    }
    private fun showDiscoveredDevices(devices: List<BluetoothDevice>) {
        hideProgress()
        if (devices.isNotEmpty()) {
            // Se quiser listar os dispositivos sem nome, mostrar o endereço.
            val devicesFound = devices.map { it.name ?: it.address }.toTypedArray()
            // Se não quiser listar os dispositivos sem nome
            //val devicesFound = devices.filter { !it.name.isNullOrEmpty() }.toTypedArray()

            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.devices_found)
                .setSingleChoiceItems(devicesFound, -1) { dialog, which ->
                    startClientThread(which)
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        } else {
            Toast.makeText(this, R.string.msg_no_devices_found, Toast.LENGTH_SHORT).show()
        }
    }
    private fun startClientThread(index: Int) {
        stopAll()
        val uiHandler = UiHandler(this::onMessageReceived, this::onConnectionChanged)
        btThread = BtThreadClient(remoteDevices[index], uiHandler)
        btThread?.startThread()
    }
    private fun showProgress(@StringRes message: Int,
                             timeout: Long = 0,
                             cancelClick: (() -> Unit)? = null) {
        vwProgressContainer.visibility = View.VISIBLE
        txtProgressMessage.setText(message)
        btnCancel.setOnClickListener {
            hideProgress()
            cancelClick?.invoke()
        }
        if (timeout > 0) {
            vwProgressContainer.postDelayed({
                hideProgress()
                cancelClick?.invoke()
            }, timeout)
        }
    }
    private fun hideProgress() {
        vwProgressContainer.visibility = View.GONE
    }
    private fun onMessageReceived(message: String) {
        messagesAdapter?.add(message)
        messagesAdapter?.notifyDataSetChanged()
    }
    private fun onConnectionChanged(connected: Boolean) {
        hideProgress()
        if (connected) {
            Toast.makeText(this,
                R.string.msg_connected,
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,
                R.string.msg_disconnected,
                Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendButtonClick() {
        val msg = edtMessage.text.toString()
        try {
            btThread?.sendMessage(msg)
            messagesAdapter?.add(getString(R.string.my_message, msg))
            messagesAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        edtMessage.text.clear()
    }
    private inner class BtEventsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
                remoteDevices.add(device)
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent.action) {
                showDiscoveredDevices(remoteDevices)
            }
        }
    }
    companion object {
        private const val BT_ACTIVATE = 0
        private const val BT_VISIBLE = 1
        private const val BT_DISCOVERY_TIME = 120
        private const val RC_LOCATION_PERMISSION = 2
    }
}

