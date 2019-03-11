package dominando.android.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context,
            intent.getStringExtra(EXTRA_MESSAGE),
            Toast.LENGTH_LONG).show()
        NotificationManagerCompat.from(context).cancelAll()
    }
    companion object {
        const val EXTRA_MESSAGE = "message"
    }
}
