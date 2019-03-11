package dominando.android.multimidia

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_record_audio.*
import java.io.File
import java.io.IOException

class RecordAudioFragment : MultimediaFragment() {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFile: File? = null
    private var isRecording: Boolean = false
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val audioPath = MediaUtils.getLastMediaPath(
            requireContext(), MediaUtils.MediaType.MEDIA_AUDIO)
        if (audioPath != null) {
            audioFile = File(audioPath)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record_audio, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRecord.setOnClickListener {
            chronometer.stop()
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
            updateButtons()
        }
        btnPlay.setOnClickListener {
            chronometer.stop()
            if (isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
            updateButtons()
        }
    }
    override fun onPause() {
        super.onPause()
        stopRecording()
        stopPlaying()
    }
    private fun startPlaying() {
        if (audioFile?.exists() == true) {
            if (hasPermission()) {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioFile?.absolutePath)
                    }
                    mediaPlayer?.setOnCompletionListener {
                        isPlaying = false
                        chronometer.stop()
                        updateButtons()
                    }
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.start()
                    isPlaying = true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                requestPermissions()
            }
        }
    }
    private fun startRecording() {
        if (hasPermission()) {
            try {
                audioFile = MediaUtils.newMedia(MediaUtils.MediaType.MEDIA_AUDIO)
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(audioFile?.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                }
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                isRecording = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requestPermissions()
        }
    }
    private fun updateButtons() {
        btnRecord.setImageResource(
            if (isRecording)
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_btn_speak_now)
        btnRecord.isEnabled = !isPlaying
        btnPlay.setImageResource(
            if (isPlaying)
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play)
        btnPlay.isEnabled = !isRecording
    }
    private fun stopPlaying() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }
    private fun stopRecording() {
        if (mediaRecorder != null && isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            audioFile?.let {
                MediaUtils.saveLastMediaPath(requireContext(),
                    MediaUtils.MediaType.MEDIA_AUDIO, it.absolutePath)
            }
        }
    }
}
