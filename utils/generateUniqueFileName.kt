package com.example.soundmixer.utils

import android.content.Context
import java.io.File

class GenerateUniqueFileName(context: Context){

    val context = context

    fun generateUniqueAudioFileName(): String {
        val baseName = "audio"
        val extension = ".mp3"
        val cacheDir = context.cacheDir

        var fileName: String
        var fileIndex = 0

        do {
            fileName = if (fileIndex == 0) {
                "$baseName$extension"
            } else {
                "${baseName}_$fileIndex$extension"
            }
            fileIndex++
        } while (File(cacheDir, fileName).exists())

        return fileName
    }

    fun generateUniqueMergedFileName(): String {
        val baseName = "merge"
        val extension = ".mp3"
        val cacheDir = context.filesDir

        var fileName: String
        var fileIndex = 0

        do {
            fileName = if (fileIndex == 0) {
                "$baseName$extension"
            } else {
                "${baseName}_$fileIndex$extension"
            }
            fileIndex++
        } while (File(cacheDir, fileName).exists())

        return fileName
    }
}
