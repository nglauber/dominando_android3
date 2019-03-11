package dominando.android.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Handler

class BtThreadClient(
    private val device: BluetoothDevice,
    uiHandler: Handler) : BtThread(uiHandler) {

    override fun run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(SERVICE_UUID)
            socket?.connect()
            threadCommunication.handleConnection(socket!!)
        } catch (e: Exception) {
            e.printStackTrace()
            uiHandler.obtainMessage(BtThreadCommunication.MSG_DISCONNECTED, "${e.message} [2]")?.sendToTarget()
        }
    }
}
