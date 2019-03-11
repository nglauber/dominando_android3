package dominando.android.http

import android.app.PendingIntent
import android.net.ConnectivityManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.fragment.app.Fragment

abstract class InternetFragment : Fragment() {
    private var receiver: ConnectionReceiver? = ConnectionReceiver()
    private val pit: PendingIntent by lazy {
        PendingIntent.getBroadcast(context,
            0, Intent(ACTION_CONNECTIVITY), 0)
    }

    abstract fun startDownload()

    override fun onResume() {
        super.onResume()
        registerForConnectivityChanges()
    }
    override fun onPause() {
        super.onPause()
        unregisterForConnectivityChanges()
    }

    private fun isMarshmallowOrLater() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private fun registerForConnectivityChanges() {
        if (isMarshmallowOrLater()) {
            val context = requireContext()
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .build()
            connectivityManager.registerNetworkCallback(request, pit)
            val filter = IntentFilter(ACTION_CONNECTIVITY)
            context.registerReceiver(receiver, filter)
        } else {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context?.registerReceiver(receiver, filter)
        }
    }
    private fun unregisterForConnectivityChanges() {
        if (isMarshmallowOrLater()) {
            val context = requireContext()
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(pit)
        } else {
            context?.unregisterReceiver(receiver)
        }
    }
    internal inner class ConnectionReceiver : BroadcastReceiver() {
        private var first = true
        override fun onReceive(context: Context, intent: Intent) {
            var validAction = false
            if (isMarshmallowOrLater()) {
                if (intent.action == ACTION_CONNECTIVITY) {
                    validAction = true
                }
            } else {
                if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                    if (first) {
                        first = false
                    } else {
                        validAction = true
                    }
                }
            }
            if (validAction && BookHttp.hasConnection(context)) {
                startDownload()
            }
        }
    }
    companion object {
        const val ACTION_CONNECTIVITY = "dominando.android.http.CONNECTION"
    }
}
