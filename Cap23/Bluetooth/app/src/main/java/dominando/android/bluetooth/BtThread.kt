package dominando.android.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler

abstract class BtThread(protected val uiHandler: Handler) : Thread() {
    var socket: BluetoothSocket? = null
    var threadCommunication = BtThreadCommunication(uiHandler)

    fun sendMessage(message: String) {
        threadCommunication.sendMessage(message)
    }
    open fun startThread() {
        start()
    }
    open fun stopThread() {
        try {
            threadCommunication.closeConnection()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
