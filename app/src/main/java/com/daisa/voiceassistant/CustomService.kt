package com.daisa.voiceassistant

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast

class CustomService : Service(), RecognitionListener {

    private val TAG = "VOICEASSISTANT"

    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    override fun onCreate() {
        Log.d(TAG, "This is a Service running in Background")

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech.setRecognitionListener(this)

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        speech.startListening(recognizerIntent)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        Log.d(TAG, "CustomService task removed")
        super.onTaskRemoved(rootIntent)
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.d(TAG, "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Beggining for speech")
    }

    override fun onRmsChanged(value: Float) {
        //Log.d(TAG, "Rms changed to $value")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "End for speech")
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d(TAG, "FAILED $errorMessage")
        onTaskRemoved(recognizerIntent)
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
        Log.i(TAG, "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        Log.d(TAG, "Results: $text")
        onTaskRemoved(recognizerIntent)

    }

    override fun onPartialResults(p0: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        TODO("Not yet implemented")
    }
}