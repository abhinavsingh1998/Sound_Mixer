package com.example.soundmixer.repository

import androidx.lifecycle.LiveData
import com.example.soundmixer.data_base.Recording
import com.example.soundmixer.data_base.RecordingDao
import com.example.soundmixer.features.search.data_models.FileDetailsResponse
import com.example.soundmixer.features.search.data_models.SearchResponse

import com.example.soundmixer.network.ApiService
import com.example.soundmixer.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


import retrofit2.Response
import java.io.File


class SoundRepository (private val dao: RecordingDao){

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    // Function to search for sounds based on a query
    suspend fun searchSound(query: String): Response<SearchResponse> {
        return apiService.searchSounds(query = "$query+sounds")
    }

    // Function to fetch file details, including the download URL
    suspend fun fetchFileDetails(fileName: String): Response<FileDetailsResponse>? {

        return try {
            val response = apiService.getFileDetails(titles = fileName)
            if (response.isSuccessful) {
                response
            } else {
                // Handle error case appropriately (e.g., log the error)
                null
            }
        } catch (e: Exception) {
            // Handle the exception (e.g., log it or show a user-friendly message)
            null
        }
    }

    // Function to download the file and save metadata to Room DB
    suspend fun downloadAndSaveFile(fileUrl: String, fileName: String): Boolean {
        return try {
            val request = Request.Builder()
                .url(fileUrl)
                .build()

            val response = OkHttpClient().newCall(request).execute()

            if (response.isSuccessful) {
                val fileBytes = response.body?.bytes()
                if (fileBytes != null) {
                    // Save the file locally
                    val localPath = saveFileLocally(fileBytes, fileName)

                    // Save file metadata in Room DB
                    val downloadedFile = Recording (
                        fileName = fileName,
                        filePath = localPath.toString()
                    )
                    dao.insert(downloadedFile)

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            // Handle the exception (e.g., log it)
            false
        }
    }

    fun getAllRecordings(): LiveData<List<Recording>> = dao.getAllRecordedFiles()

    // Function to save the downloaded file locally
    private fun saveFileLocally(fileBytes: ByteArray, fileName: String) {
        val file = File("path/to/save/directory", fileName)
        file.outputStream().use { outputStream ->
            outputStream.write(fileBytes)
        }
    }

    suspend fun insert(recording: Recording) {
        dao.insert(recording)
    }

    suspend fun deleteById(fileId: Int) {
        withContext(Dispatchers.IO) {
            dao.deleteById(fileId)
        }
    }
}


