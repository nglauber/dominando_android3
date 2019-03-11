package dominando.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.os.Handler
import java.io.IOException

class BtThreadServer(
    private val btAdapter: BluetoothAdapter?,
    uiHandler: Handler) : BtThread(uiHandler) {
    var serverSocket: BluetoothServerSocket? = null

    override fun run() {
        try {
            serverSocket = btAdapter?.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID)
            socket = serverSocket?.accept()
            threadCommunication.handleConnection(socket!!)

        } catch (e: Exception) {
            uiHandler.obtainMessage(
                BtThreadCommunication.MSG_DISCONNECTED,
                "${e.message} #1")?.sendToTarget()
            e.printStackTrace()
        }
    }
    override fun stopThread() {
        super.stopThread()
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
