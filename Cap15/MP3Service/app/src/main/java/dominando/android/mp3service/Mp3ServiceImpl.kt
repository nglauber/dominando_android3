package dominando.android.mp3service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.File
import java.io.FileInputStream

class Mp3ServiceImpl : Service(), Mp3Service {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPaused: Boolean = false
    private var currentFile: String? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder? {
        return Mp3Binder(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.getStringExtra(EXTRA_ACTION)) {
                ACTION_PLAY -> play(intent.getStringExtra(EXTRA_FILE))
                ACTION_PAUSE -> pause()
                ACTION_STOP -> stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Implementação da interface Mp3Service
    override fun play(file: String) {
        if (!mediaPlayer.isPlaying && !isPaused) {
            try {
                mediaPlayer.reset()
                val fis = FileInputStream(file)
                mediaPlayer.setDataSource(fis.fd)
                mediaPlayer.prepare()
                currentFile = file
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
        isPaused = false
        mediaPlayer.start()
        createNotification()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            isPaused = true
            mediaPlayer.pause()
        }
    }

    override fun stop() {
        if (mediaPlayer.isPlaying || isPaused) {
            isPaused = false
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        removeNotification()
    }

    override var currentSong: String? = null
        get() = currentFile

    override var totalTime: Int = 0
        get() {
            return if (mediaPlayer.isPlaying || isPaused) {
                mediaPlayer.duration
            } else 0
        }

    override var elapsedTime: Int = 0
        get() {
            return if (mediaPlayer.isPlaying || isPaused) {
                mediaPlayer.currentPosition
            } else 0
        }

    private fun createNotification() {
        val channelId = "channel1"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW)
            )
        }
        val itPlay = Intent(this, Mp3ServiceImpl::class.java).apply {
            putExtra(EXTRA_ACTION, ACTION_PLAY)
            putExtra(EXTRA_FILE, currentFile)
        }
        val itPause = Intent(this, Mp3ServiceImpl::class.java).apply {
            putExtra(EXTRA_ACTION, ACTION_PAUSE)
        }
        val itStop = Intent(this, Mp3ServiceImpl::class.java).apply {
            putExtra(EXTRA_ACTION, ACTION_STOP)
        }
        val pitActivity = PendingIntent.getActivity(this, 0,
            Intent(this, Mp3Activity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pitPlay = PendingIntent.getService(this, 1, itPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        val pitPause = PendingIntent.getService(this, 2, itPause, 0)
        val pitStop = PendingIntent.getService(this, 3, itStop, 0)
        val views = RemoteViews(packageName, R.layout.layout_notification)
        views.setOnClickPendingIntent(R.id.btnPlay, pitPlay)
        views.setOnClickPendingIntent(R.id.btnPause, pitPause)
        views.setOnClickPendingIntent(R.id.btnClose, pitStop)
        views.setOnClickPendingIntent(R.id.txtMusic, pitActivity)
        views.setTextViewText(R.id.txtMusic,
            currentFile?.substring((currentFile?.lastIndexOf(File.separator) ?: 0) + 1))
        val n = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContent(views)
            .setOngoing(true)
            .build()
        NotificationManagerCompat.from(this).notify(1, n)
    }

    private fun removeNotification() {
        NotificationManagerCompat.from(this).cancel(1)
    }

    companion object {
        val EXTRA_ACTION = "${Mp3ServiceImpl::class.java.`package`.name}.EXTRA_ACTION"
        val EXTRA_FILE = "${Mp3ServiceImpl::class.java.`package`.name}.EXTRA_FILE"
        val ACTION_PLAY = "${Mp3ServiceImpl::class.java.`package`.name}.ACTION_PLAY"
        val ACTION_PAUSE = "${Mp3ServiceImpl::class.java.`package`.name}.ACTION_PAUSE"
        val ACTION_STOP = "${Mp3ServiceImpl::class.java.`package`.name}.ACTION_STOP"
    }
}

