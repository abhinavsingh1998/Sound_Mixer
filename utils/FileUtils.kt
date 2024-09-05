package com.example.soundmixer.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {

    fun saveFile(context: Context, inputStream: InputStream, fileName: String): File {
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
        return file
    }
}