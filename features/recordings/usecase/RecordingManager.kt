package com.example.soundmixer.features.recordings.usecase

import java.io.File

interface RecordingManager {
    fun startRecording(outputFile: File)
    fun stopRecording()
}

