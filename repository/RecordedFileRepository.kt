package com.example.soundmixer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.soundmixer.data_base.Recording
import com.example.soundmixer.data_base.RecordingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordedFileRepository(private val dao: RecordingDao) {

    fun getAllRecordings(): LiveData<List<Recording>> = dao.getAllRecordedFiles()

    suspend fun insert(recording: Recording) {
        dao.insert(recording)
    }

    suspend fun deleteById(fileId: Int) {
        withContext(Dispatchers.IO) {
          dao.deleteById(fileId)
        }
    }
}