package dominando.android.multimidia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_voice_recognition.*
import java.util.*

class VoiceRecognitionFragment : MultimediaFragment() {

    private var isRecognizing = false

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(activity)
    }

    private val voiceIntent: Intent by lazy {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Fale alguma coisa!")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_voice_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnVoice.setOnClickListener {
            openVoiceRecognitionIntent()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (voiceIntent.resolveActivity(requireActivity().packageManager) == null) {
            btnVoice.isEnabled = false
            Toast.makeText(activity, "Aparelho não suporta comando de voz.",
                Toast.LENGTH_SHORT).show()
            activity?.finish()
        } else {
            speechRecognizer.setRecognitionListener(recognitionListener)
        }
    }

    override fun onPause() {
        super.onPause()
        speechRecognizer.destroy()
    }

    private fun openVoiceRecognitionIntent() {
        if (chkIntent.isChecked) {
            startActivityForResult(voiceIntent, MediaUtils.REQUEST_CODE_AUDIO)
        } else {
            if (hasPermission()) {
                if (isRecognizing) {
                    stopRecognizing()
                } else {
                    startRecognizing()
                }
            } else {
                requestPermissions()
            }
        }
    }

    private fun startRecognizing() {
        pgrVoice.visibility = View.VISIBLE
        pgrVoice.isIndeterminate = true
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        speechRecognizer.startListening(voiceIntent)
        isRecognizing = true
    }

    private fun stopRecognizing() {
        pgrVoice.visibility = View.INVISIBLE
        pgrVoice.isIndeterminate = false
        speechRecognizer.stopListening()
        isRecognizing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MediaUtils.REQUEST_CODE_AUDIO && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS)
            updateResultsList(matches)
        }
    }

    private fun updateResultsList(results: ArrayList<String>?) {
        if (results != null) {
            lstResults.adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_list_item_1, results)
        }
        pgrVoice.visibility = View.INVISIBLE
        isRecognizing = false
    }

    private val recognitionListener = object: RecognitionListener {
        override fun onBeginningOfSpeech() {
            pgrVoice.isIndeterminate = false
            pgrVoice.max = 10
        }
        override fun onEndOfSpeech() {
            pgrVoice.isIndeterminate = true
        }
        override fun onRmsChanged(v: Float) {
            pgrVoice.progress = v.toInt()
        }
        override fun onError(i: Int) {
            Toast.makeText(activity, "Problemas no comando de voz. Erro: $i",
                Toast.LENGTH_SHORT).show()
            pgrVoice.visibility = View.INVISIBLE
        }
        override fun onResults(bundle: Bundle) {
            val results = bundle.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION)
            updateResultsList(results)
        }
        override fun onPartialResults(bundle: Bundle) {
        }
        override fun onReadyForSpeech(bundle: Bundle) {
        }
        override fun onBufferReceived(bytes: ByteArray) {
        }
        override fun onEvent(i: Int, bundle: Bundle) {
        }
    }
}
