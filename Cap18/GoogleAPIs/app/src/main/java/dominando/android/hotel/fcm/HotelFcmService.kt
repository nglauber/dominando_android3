package dominando.android.hotel.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dominando.android.hotel.R
import dominando.android.hotel.common.HotelActivity
import dominando.android.hotel.repository.http.HotelSyncWorker
import org.koin.android.ext.android.inject

class HotelFcmService : FirebaseMessagingService() {
    private val tokenManager: TokenManager by inject()
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        HotelSyncWorker.start()
        val title = getString(R.string.app_name)
        val message = getString(R.string.notification_text)
        if (remoteMessage.data.isNotEmpty()) {
            triggerNotification(title, remoteMessage.data["message"] ?: message)
        } else if (remoteMessage.notification != null) {
            triggerNotification(remoteMessage.notification?.body ?: title, message)
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        tokenManager.updateToken(token)
    }
    private fun triggerNotification(title: String, msg: String) {
        val notificationId = 1
        val channelId = "channel1"
        createChannel(channelId, title)
        val notificationManager = NotificationManagerCompat.from(this)
        val it = Intent(this, HotelActivity::class.java)
        val pit = PendingIntent.getActivity(this,
            0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, channelId)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(msg)
            .setContentIntent(pit)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true)
        notificationManager.notify(notificationId, builder.build())
    }
    private fun createChannel(channelId: String, title: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }
}
