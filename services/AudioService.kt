package com.example.soundmixer.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.soundmixer.R
import com.example.soundmixer.features.playback.usecase.ExoPlayerManager
import com.example.soundmixer.features.recordings.usecase.MediaRecorderManager
import com.example.soundmixer.utils.GenerateUniqueFileName
import java.io.File

class AudioService : Service() {

    // Initialize the MediaRecorderManager and MediaPlayerManager lazily
    private val recorder by lazy { MediaRecorderManager(this) }
    private val player by lazy { ExoPlayerManager(this) }

    // Variable to hold the current recording file
    private var audioFile: File? = null

    // Variable to hold the file path for playback
    private var playbackFilePath: String? = null

    // Required by Service class, even if the service is not intended to be bound
    override fun onBind(intent: Intent?): IBinder? {
        // Return null because this service is not bound
        return null
    }

    // Called when a client explicitly starts the service using startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AudioService", "Service Started with action: ${intent?.action}")

        // Determine which action to perform based on the intent's action
        when (intent?.action) {
            ACTION_START_RECORDING -> startRecording()
            ACTION_STOP_RECORDING -> stopRecording()
            ACTION_START_PLAYBACK -> {
                playbackFilePath = intent.getStringExtra(EXTRA_FILE_PATH)
                Log.d("AudioService", "Playback file path: $playbackFilePath")
                playbackFilePath?.let { startPlayback(it) }
            }
            ACTION_STOP_PLAYBACK -> stopPlayback()
        }

        // If the service is killed by the system, it won't be recreated
        return START_NOT_STICKY
    }

    // Starts recording audio and saves it to a unique file in the cache directory
    private fun startRecording() {
        audioFile = File(cacheDir, GenerateUniqueFileName(this).generateUniqueAudioFileName()).apply {
            recorder.startRecording(this)
        }
        showNotification("Recording Audio")
        Log.d("AudioService", "Started Recording")
    }

    // Stops the recording and clears the foreground notification
    private fun stopRecording() {
        Log.d("AudioService", "Stopping Recording: $audioFile")
        recorder.stopRecording()
        stopForeground(true)
        Log.d("AudioService", "Stopped Recording")
    }

    // Starts playback of the specified file
    private fun startPlayback(filePath: String) {
        val file = filePath
        if (!file.isNullOrEmpty()) {
            player.play(file)
            showNotification("Playing Audio")
            Log.d("AudioService", "Started Playback")
        } else {
            Log.e("AudioService", "File not found: $filePath")
        }
    }

    // Stops playback and clears the foreground notification
    private fun stopPlayback() {
        player.stop()
        stopForeground(true)
        Log.d("AudioService", "Stopped Playback")
    }

    // Shows a notification for the audio service
    private fun showNotification(contentText: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Service")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }



    companion object {
        // Constants for actions and extras used in intents
        const val ACTION_START_RECORDING = "com.codility.voicerecorder.ACTION_START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.codility.voicerecorder.ACTION_STOP_RECORDING"
        const val ACTION_START_PLAYBACK = "com.codility.voicerecorder.ACTION_START_PLAYBACK"
        const val ACTION_STOP_PLAYBACK = "com.codility.voicerecorder.ACTION_STOP_PLAYBACK"
        const val EXTRA_FILE_PATH = "extra_file_path"

        // Constants for notification channel ID and notification ID
        private const val CHANNEL_ID = "audio_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}




