package dominando.android.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, it: Intent) {
        val sms = getMessagesFromIntent(it)[0]
        val phoneAddress = sms?.originatingAddress
        val message = sms?.messageBody
        Toast.makeText(context,
            "Mensagem recebida de $phoneAddress - $message",
            Toast.LENGTH_LONG).show()
    }
    companion object {
        fun getMessagesFromIntent(intent: Intent): Array<SmsMessage?> {
            val pdus = intent.getSerializableExtra("pdus") as Array<*>
            val messages = arrayOfNulls<SmsMessage>(pdus.size)
            for (i in pdus.indices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val format = intent.getStringExtra("format")
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                } else {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
            }
            return messages
        }
    }
}
