package dominando.android.sms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var smsSenderBroadcast: SmsSenderBroadcast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.phoneType == TelephonyManager.PHONE_TYPE_NONE) {
            Toast.makeText(this, "Dispositivo não suporta SMS",
                Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    override fun onResume() {
        super.onResume()
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
        )
        val hasPermissions = permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_SMS)
        }
        smsSenderBroadcast = SmsSenderBroadcast()
        registerReceiver(smsSenderBroadcast, IntentFilter(ACTION_SENT))
        registerReceiver(smsSenderBroadcast, IntentFilter(ACTION_DELIVERED))
    }
    override fun onPause() {
        super.onPause()
        if (smsSenderBroadcast != null) {
            unregisterReceiver(smsSenderBroadcast)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Você necessita aceitar as permissões.",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    fun sendSmsClick(v: View) {
        val pitSent = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_SENT), 0)
        val pitDelivered = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_DELIVERED), 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            edtPhoneNumber.text.toString(), null,
            edtMessage.text.toString(),
            pitSent,
            pitDelivered)
    }
    class SmsSenderBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var message: String? = null
            val action = intent.action
            val result = resultCode
            if (result == Activity.RESULT_OK) {
                if (ACTION_SENT == action) {
                    message = "Enviado com sucesso."
                } else if (ACTION_DELIVERED == action) {
                    message = "Entregue com sucesso."
                }
            } else {
                if (ACTION_SENT == action) {
                    message = "Falha ao enviar: $result"
                } else if (ACTION_DELIVERED == action) {
                    message = "Falhar ao entregar: $result"
                }
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_SMS = 1
        private const val ACTION_SENT = "sms_enviado"
        private const val ACTION_DELIVERED = "sms_entregue"
    }
}

