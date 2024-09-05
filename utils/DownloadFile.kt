package com.example.soundmixer.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.example.soundmixer.data_base.AppDatabase
import com.example.soundmixer.data_base.Recording
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun downloadFile(context: Context, fileName: String, fileUrl: String) {
    val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
        setTitle(fileName) // Title of the download notification
        setDescription("Downloading $fileName") // Description of the download notification
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Notify when the download is completed
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // Save file to Downloads folder
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)

    Toast.makeText(context, "Downloading $fileName", Toast.LENGTH_SHORT).show()

    // Register a BroadcastReceiver to listen for download completion
    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                val fileUri = Uri.parse(uriString)
                val filePath = fileUri.path ?: ""

                // Example file size, you can get it from DownloadManager or other sources
                val fileSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                val statusString = when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> "Downloaded"
                    DownloadManager.STATUS_FAILED -> "Failed"
                    else -> "Unknown"
                }

                // Save file metadata to Room DB
                CoroutineScope(Dispatchers.IO).launch {
                    val downloadedFile = Recording(
                        fileName = fileName,
                        filePath = filePath,
                    )

                    val db = AppDatabase.getDatabase(context!!)
                    db.recordingDao().insert(downloadedFile)
                }
            }
            cursor.close()
        }
    }

    context.registerReceiver(receiver, filter)
}
