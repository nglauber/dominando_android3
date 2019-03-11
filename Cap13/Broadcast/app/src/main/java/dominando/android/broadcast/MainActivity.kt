package dominando.android.broadcast

import android.content.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val receiver: InternalReceiver = InternalReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSend.setOnClickListener {
            sendImplicitBroadcast()
        }
        btnLocal.setOnClickListener {
            val intent = Intent(ACTION_EVENT)
            LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent)
        }
    }

    private fun sendImplicitBroadcast() {
        val intent = Intent(ACTION_EVENT)
        val matches = packageManager.queryBroadcastReceivers(intent, 0)
        for (resolveInfo in matches) {
            val explicit = Intent(intent)
            val componentName = ComponentName(
                resolveInfo.activityInfo.applicationInfo.packageName,
                resolveInfo.activityInfo.name
            )
            explicit.component = componentName
            sendBroadcast(explicit)
        }
    }

    override fun onResume() {
        super.onResume()
        val filterLocal = IntentFilter(ACTION_EVENT)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, filterLocal)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(receiver)
    }

    inner class InternalReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            txtMessage.text = "Ação:\n${intent.action}"
        }
    }

    companion object {
        private const val ACTION_EVENT = "dominando.android.broadcast.ACTION_EVENT"
    }
}
