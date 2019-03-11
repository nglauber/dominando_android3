package dominando.android.bluetooth

import android.os.Handler
import android.os.Message

class UiHandler(
    private val messageReceivedCallback: (String) -> Unit,
    private val connectionChangeCallback: (Boolean) -> Unit) : Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            BtThreadCommunication.MSG_TEXT ->
                messageReceivedCallback(msg.obj.toString())
            BtThreadCommunication.MSG_CONNECTED ->
                connectionChangeCallback(true)
            BtThreadCommunication.MSG_DISCONNECTED ->
                connectionChangeCallback(false)
        }
    }
}