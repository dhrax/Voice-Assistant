package com.daisa.voiceassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daisa.voiceassistant.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), RecognitionListener {
    private val permission = 100
    private lateinit var returnedText: TextView
    private lateinit var toggleButton: ToggleButton
    private lateinit var progressBar: ProgressBar
    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var logTag = "VoiceRecognitionActivity"

    private lateinit var binding : ActivityMainBinding
    private val TAG = "VOICEASSISTANT"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        title = "KotlinApp"


        val button = binding.button
        button.setOnClickListener { startService(Intent(applicationContext, CustomService::class.java)) }

        returnedText = binding.textView
        progressBar = binding.progressBar
        toggleButton = binding.toggleButton
        progressBar.visibility = View.VISIBLE
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressBar.visibility = View.VISIBLE
                progressBar.isIndeterminate = true
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    permission)
            } else {
                progressBar.isIndeterminate = false
                progressBar.visibility = View.VISIBLE
                speech.stopListening()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent)
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        speech.destroy()
        Log.i(logTag, "destroy")
    }
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech not  yet implemented")
    }
    override fun onRmsChanged(rmsdB: Float) {
        progressBar.progress = rmsdB.toInt()
    }
    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "onReadyForSpeech not  yet implemented")
    }
    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(TAG, "onReadyForSpeech not  yet implemented")
    }
    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech not  yet implemented")
    }
    override fun onBeginningOfSpeech() {
        Log.i(logTag, "onBeginningOfSpeech")
        progressBar.isIndeterminate = false
        progressBar.max = 10
    }
    override fun onEndOfSpeech() {
        progressBar.isIndeterminate = true
        toggleButton.isChecked = false
    }
    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d(logTag, "FAILED $errorMessage")
        returnedText.text = errorMessage
        toggleButton.isChecked = false
    }
    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }
    override fun onResults(results: Bundle?) {
        Log.i(logTag, "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText.text = text
    }
}