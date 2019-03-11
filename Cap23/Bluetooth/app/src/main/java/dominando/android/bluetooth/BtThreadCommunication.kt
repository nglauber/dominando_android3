package dominando.android.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Handler
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class BtThreadCommunication(private val uiHandler: Handler) {
    private var inputStream: DataInputStream? = null
    private var outputStream: DataOutputStream? = null

    fun handleConnection(socket: BluetoothSocket) {
        try {
            uiHandler.obtainMessage(MSG_CONNECTED).sendToTarget()
            val deviceName = socket.remoteDevice?.name
            inputStream = DataInputStream(socket.inputStream)
            outputStream = DataOutputStream(socket.outputStream)
            var string: String?
            while (true) {
                string = inputStream?.readUTF()
                uiHandler.obtainMessage(MSG_TEXT, "$deviceName: $string")?.sendToTarget()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            uiHandler.obtainMessage(MSG_DISCONNECTED, "${e.message} #3")?.sendToTarget()
        }
    }
    fun closeConnection() {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun sendMessage(message: String) {
        outputStream?.writeUTF(message)
    }
    companion object {
        const val MSG_TEXT = 0
        const val MSG_CONNECTED = 1
        const val MSG_DISCONNECTED = 2
    }
}
