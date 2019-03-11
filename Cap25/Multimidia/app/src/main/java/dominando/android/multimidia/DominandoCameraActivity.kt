package dominando.android.multimidia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_dominando_camera.*
import kotlinx.coroutines.*
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class DominandoCameraActivity : Activity(), CoroutineScope {
    private var camera: Camera? = null
    private var surfacePreview: CameraSurfaceView? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording: Boolean = false
    private var inPreviewMode: Boolean = false
    private var outputUri: Uri? = null

    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dominando_camera)
        val uri = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        if (uri != null) {
            outputUri = uri
        }
        btnCapture.setOnClickListener {
            val action = intent.action
            if (action == MediaStore.ACTION_IMAGE_CAPTURE) {
                takePicture()
            } else if (action == MediaStore.ACTION_VIDEO_CAPTURE) {
                recordVideo()
            }
        }
        job = Job()
    }
    override fun onResume() {
        super.onResume()
        if (isCameraAvailable()) {
            if (hasPermissions()) {
                openCamera()
            } else {
                requestPermissions()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        releaseMediaRecorder()
        releaseCamera()
        if (isRecording) {
            contentResolver.delete(outputUri, null, null)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        outputUri?.let { uri ->
            contentResolver.delete(uri, null, null)
        }
    }
    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO),
            REQUEST_PERMISSIONS)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            openCamera()
        }
    }

    private fun isCameraAvailable(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun openCamera() {
        try {
            camera = Camera.open()
            camera?.let { cam ->
                surfacePreview = CameraSurfaceView(this, cam).apply {
                    previewSizeReadyCallback = { w, h ->
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(rootCameraLayout)
                        constraintSet.setDimensionRatio(R.id.vwPreviewCamera, "h,$w:$h")
                        constraintSet.applyTo(rootCameraLayout)
                    }
                }
                vwPreviewCamera.addView(surfacePreview)
                val params = cam.parameters
                params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                cam.parameters = params
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseMediaRecorder() {
        mediaRecorder?.run {
            reset()
            release()
        }
        camera?.lock()
        mediaRecorder = null
    }

    private fun releaseCamera() {
        if (camera != null) {
            camera?.release()
            camera = null
            surfacePreview?.holder?.removeCallback(surfacePreview)
        }
    }

    private fun takePicture() {
        if (inPreviewMode) {
            setResult(RESULT_OK)
            finish()
        } else {
            camera?.takePicture(null, null, pictureCallback)
        }
    }

    private val pictureCallback = Camera.PictureCallback { data, camera ->
        outputUri?.let { uri ->
            try {
                val fileDescriptor = contentResolver
                    .openFileDescriptor(uri, "rw")?.fileDescriptor
                val fos = FileOutputStream(fileDescriptor)
                fos.write(data)
                fos.close()
                inPreviewMode = true
                btnCapture.text = "OK"
                camera.stopPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun recordVideo() {
        if (isRecording) {
            finishRecording()
        } else {
            launch {
                val success = withContext(Dispatchers.Default) {
                    prepareRecording()
                }
                if (success) {
                    mediaRecorder?.start()
                    btnCapture.text = "Stop"
                    isRecording = true
                } else {
                    releaseMediaRecorder()
                }
            }
        }
    }

    private fun prepareRecording(): Boolean {
        camera?.unlock()
        outputUri?.let { uri ->
            try {
                val fileDescriptor = contentResolver
                    .openFileDescriptor(uri, "rw")?.fileDescriptor

                mediaRecorder = MediaRecorder().apply {
                    setCamera(camera)
                    setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                    setVideoSource(MediaRecorder.VideoSource.CAMERA)
                    setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
                    setOutputFile(fileDescriptor)
                    setMaxDuration(60_000) // 1 minuto
                    setOnInfoListener { mr, what, extra ->
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                            finishRecording()
                        }
                    }
                    setPreviewDisplay(surfacePreview?.holder?.surface)
                }
                mediaRecorder?.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                releaseMediaRecorder()
                return false
            }
        }
        return true
    }

    private fun finishRecording() {
        mediaRecorder?.stop()
        releaseMediaRecorder()
        camera?.lock()
        isRecording = false
        val it = Intent()
        it.data = outputUri
        setResult(RESULT_OK, it)
        finish()
    }

    companion object {
        const val REQUEST_PERMISSIONS = 1
    }
}