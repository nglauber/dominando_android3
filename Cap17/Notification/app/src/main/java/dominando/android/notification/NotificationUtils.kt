package dominando.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.core.app.*
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed

object NotificationUtils {
    val CHANNEL_ID = "default"
    val URGENT_ID = "urgent"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = context.getString(R.string.notif_channel_name)
        val channelDescription = context.getString(R.string.notif_channel_description)
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = channelDescription
            enableLights(true)
            lightColor = Color.GREEN
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun notificationSimple(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val deletePit = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, DeleteNotificationReceiver::class.java), 0)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .setDeleteIntent(deletePit)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun getContentIntent(context: Context): PendingIntent? {
        val detailsIntent = Intent(context, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MESSAGE, "Via notificação")
        }
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(detailsIntent)
            .getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun notificationWithTapAction(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(getContentIntent(context))
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(2, notificationBuilder.build())
    }

    fun notificationBigText(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val bigTextStyle = NotificationCompat
            .BigTextStyle()
            .bigText(context.getString(R.string.notif_big_message))
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(getContentIntent(context))
            .setAutoCancel(true)
            .setStyle(bigTextStyle)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(3, notificationBuilder.build())
    }

    fun notificationWithButtonAction(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val actionIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            putExtra(NotificationActionReceiver.EXTRA_MESSAGE, "Ação da notificação")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, actionIntent, 0)
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .addAction(0, context.getString(R.string.notif_button_action), pendingIntent)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(4, notificationBuilder.build())
    }

    fun notificationAutoReply(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val notificationId = 5
        val intent = Intent(context, ReplyReceiver::class.java).apply {
            putExtra(ReplyReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteInput = RemoteInput.Builder(ReplyReceiver.EXTRA_TEXT_REPLY)
            .setLabel(context.getString(R.string.notif_reply_hint))
            .build()
        val action = NotificationCompat.Action.Builder(R.drawable.ic_send,
            context.getString(R.string.notif_reply_label), replyPendingIntent)
            .addRemoteInput(remoteInput)
            .build()
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .addAction(action)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun notificationReplied(context: Context, notificationId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val timeout = 2000L
        val notificationBuilder = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_reply_replied))
            .setColor(ContextCompat.getColor(context, R.color.colorAccent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(0)
            .setTimeoutAfter(timeout)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Handler().postDelayed({ notificationManager.cancel(notificationId) }, timeout)
        }
    }

    fun notificationInbox(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        val number = 5
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(context.getString(R.string.notif_big_inbox_title))
        for (i in 1..number) {
            inboxStyle.addLine(context.getString(R.string.notif_big_inbox_message, i))
        }
        inboxStyle.setSummaryText(context.getString(R.string.notif_big_inbox_summary))
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setDefaults(Notification.DEFAULT_ALL)
            .setNumber(number)
            .setStyle(inboxStyle)
        val nm = NotificationManagerCompat.from(context)
        nm.notify(8, notificationBuilder.build())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationUrgentChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            URGENT_ID,
            context.getString(R.string.notif_channel_urgent_name),
            NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }
    fun notificationHeadsUp(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationUrgentChannel(context)
        }
        val notificationBuilder = NotificationCompat.Builder(context, URGENT_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(context.getString(R.string.notif_text))
            .setColor(ActivityCompat.getColor(context, R.color.colorAccent))
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setFullScreenIntent(getContentIntent(context), true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuilder.build())
    }
}

