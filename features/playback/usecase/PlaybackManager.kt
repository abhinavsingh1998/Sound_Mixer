package com.example.soundmixer.features.playback.usecase

interface PlaybackManager {
  fun play(fileOrUrl: String)
  fun stop()
}

