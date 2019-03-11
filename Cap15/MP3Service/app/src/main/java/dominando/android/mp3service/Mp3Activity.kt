package dominando.android.mp3service

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.database.Cursor
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleCursorAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.text.format.DateUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import kotlinx.android.synthetic.main.activity_mp3.*

class Mp3Activity : AppCompatActivity(),
    ServiceConnection,
    AdapterView.OnItemClickListener,
    LoaderManager.LoaderCallbacks<Cursor> {

    private var service: Mp3Service? = null
    private var music: String = ""
    private val columns = arrayOf(
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns._ID
    )
    private val adapter: SimpleCursorAdapter by lazy {
        SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_2,
            null,
            columns,
            intArrayOf(android.R.id.text1, android.R.id.text2),
            0)
    }
    private lateinit var handler: Handler
    private lateinit var threadProgress: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp3)
        handler = Handler()
        threadProgress = object : Thread() {
            override fun run() {
                updateScreen()
                if (service?.totalTime ?: 0 > service?.elapsedTime ?: 0) {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        btnPlay.setOnClickListener { play() }
        btnPause.setOnClickListener { pause() }
        btnStop.setOnClickListener { stop() }
    }
    private fun updateScreen() {
        music = service?.currentSong ?: ""
        txtSong.text = music
        val elapsedTime = service?.elapsedTime ?: 0
        txtTime.text = DateUtils.formatElapsedTime(elapsedTime.toLong() / 1000)
        progressBar.max = service?.totalTime ?: 0
        progressBar.progress = elapsedTime
    }
    private fun play() {
        handler.removeCallbacks(threadProgress)
        if (music.isNotBlank()) {
            service?.play(music)
            handler.post(threadProgress)
        }
    }
    private fun pause() {
        service?.pause()
        handler.removeCallbacks(threadProgress)
    }
    private fun stop() {
        service?.stop()
        handler.removeCallbacks(threadProgress)
        progressBar.progress = 0
        txtTime.text = DateUtils.formatElapsedTime(0)
    }
    override fun onResume() {
        super.onResume()
        if (adapter.count == 0) {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ActivityCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED) {
                LoaderManager.getInstance(this).initLoader(0, null, this)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            }
        }
        val intent = Intent(this, Mp3ServiceImpl::class.java)
        startService(intent)
        bindService(intent, this, 0)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LoaderManager.getInstance(this).initLoader(0, null, this)
        } else {
            Toast.makeText(this, "Permissão negada.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    override fun onPause() {
        super.onPause()
        unbindService(this)
        handler.removeCallbacks(threadProgress)
    }
    // ServiceConnection
    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as Mp3Binder).service
        handler.post(threadProgress)
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }
    // AdapterView.OnItemClickListener
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = parent?.getItemAtPosition(position) as? Cursor
        cursor?.let {
            music = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            stop()
            play()
        }
    }
    // LoaderManager.LoaderCallbacks<Cursor>
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            columns,
            MediaStore.Audio.AudioColumns.IS_MUSIC + " = 1",
            null,
            null
        )
    }
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        adapter.swapCursor(data)
        listView.adapter = adapter
        listView.onItemClickListener = this
    }
    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.swapCursor(null)
    }
}
