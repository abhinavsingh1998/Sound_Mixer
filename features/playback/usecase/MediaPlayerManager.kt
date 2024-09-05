package com.example.soundmixer.features.playback.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.File

class ExoPlayerManager(private val context: Context) : PlaybackManager {

    private var player: ExoPlayer? = null

    override fun play(fileOrUrl: String) {
        // Release any existing player to avoid conflicts
        player?.release()

        // Initialize a new ExoPlayer instance
        player = ExoPlayer.Builder(context).build().apply {
            try {
                // Check if the input is a local file path or a URL
                val uri = if (fileOrUrl.startsWith("/")) {
                    // File path
                    Uri.fromFile(File(fileOrUrl))
                } else {
                    // URL
                    Uri.parse(fileOrUrl)
                }

                // Set the media item to play
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()  // Prepare the player
                playWhenReady = true  // Start playback
            } catch (e: Exception) {
                Log.e("ExoPlayerManager", "Error setting media item or preparing ExoPlayer: ${e.message}")
            }
        }

        // Check if the player is null after initialization
        if (player == null) {
            Log.e("ExoPlayerManager", "ExoPlayer is not initialized properly")
        }
    }

    override fun stop() {
        player?.apply {
            playWhenReady = false  // Stop playback
            release()  // Release the player resources
        }
        player = null
    }
}


