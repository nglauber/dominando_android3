package dominando.android.multimidia

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.MediaController
import kotlinx.android.synthetic.main.fragment_camera_video.*

class CameraVideoFragment : MultimediaFragment(), ViewTreeObserver.OnGlobalLayoutListener {
    private var videoUri: Uri? = null
    private var position: Int = 0
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        if (videoUri == null) {
            val videoPath = MediaUtils.getLastMediaPath(
                requireActivity(), MediaUtils.MediaType.MEDIA_VIDEO)
            if (videoPath != null) {
                videoUri = Uri.parse(videoPath)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(
            R.layout.fragment_camera_video, container, false)
        layout.viewTreeObserver.addOnGlobalLayoutListener(this)
        return layout
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoView.setMediaController(MediaController(activity))
        btnVideo.setOnClickListener {
            openCamera()
        }
    }
    override fun onPause() {
        isPlaying = videoView.isPlaying
        position = videoView.currentPosition
        if (position == videoView.duration) {
            position = 0
        }
        super.onPause()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MediaUtils.REQUEST_CODE_VIDEO && resultCode == Activity.RESULT_OK) {
            loadVideo()
        }
    }
    override fun onGlobalLayout() {
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        loadVideo()
    }
    private fun openCamera() {
        if (hasPermission()) {
            activity?.let {
                try {
                    videoUri = MediaUtils.getVideoUri(requireContext())
                    val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    }
                    if (captureVideoIntent.resolveActivity(it.packageManager) != null) {
                        startActivityForResult(captureVideoIntent, MediaUtils.REQUEST_CODE_VIDEO)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            requestPermissions(RC_OPEN_CAMERA)
        }
    }
    private fun loadVideo() {
        activity?.let {
            videoUri?.let { uri ->
                if (hasPermission()) {
                    videoView.setVideoURI(uri)
                    videoView.seekTo(position)
                    if (isPlaying) {
                        videoView.start()
                    }
                    MediaUtils.saveLastMediaPath(it,
                        MediaUtils.MediaType.MEDIA_VIDEO,
                        uri.toString())
                } else {
                    requestPermissions(RC_LOAD_VIDEO)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
            when (requestCode) {
                RC_LOAD_VIDEO -> loadVideo()
                RC_OPEN_CAMERA -> openCamera()
            }
        }
    }
    companion object {
        private const val RC_LOAD_VIDEO = 1
        private const val RC_OPEN_CAMERA = 2
    }
}
