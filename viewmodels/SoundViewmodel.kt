package com.example.soundmixer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundmixer.data_base.Recording
import com.example.soundmixer.features.search.data_models.FileDetailsResponse
import com.example.soundmixer.features.search.data_models.SearchResult
import com.example.soundmixer.features.merge.data_models.SelectedSoundDetails
import com.example.soundmixer.features.recordings.data_models.SoundDetails
import com.example.soundmixer.repository.SoundRepository
import com.example.soundmixer.utils.ApiResult
import com.example.soundmixer.utils.ApiResult.Success
import com.example.soundmixer.utils.filterAudioFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SoundViewModel(
    private val repository: SoundRepository
) : ViewModel() {

    // LiveData for sound search results
    private val _soundResults = MutableLiveData<ApiResult<List<SearchResult>>>()
    val soundResults: LiveData<ApiResult<List<SearchResult>>> = _soundResults

    // LiveData for file details results
    private val _fileDetails = MutableLiveData<ApiResult<FileDetailsResponse>>()
    val fileDetails: LiveData<ApiResult<FileDetailsResponse>> = _fileDetails

    private val _downloadStatus = MutableLiveData<Boolean>()
    val downloadStatus: LiveData<Boolean> get() = _downloadStatus

    val allRecordedFiles: LiveData<List<Recording>> = repository.getAllRecordings()
    private val currentFile = MutableLiveData<SoundDetails>()
    val _currentfile:LiveData<SoundDetails> = currentFile

    // LiveData to hold selected sound details
    private val _selectedSoundDetails = MutableLiveData<SelectedSoundDetails?>()
    val selectedSoundDetails: MutableLiveData<SelectedSoundDetails?> get() = _selectedSoundDetails

    private val _setCheckBoxFlag = MutableLiveData<Boolean>()
    val getCheckBoxFlag: LiveData<Boolean> = _setCheckBoxFlag

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> get() = _elapsedTime

    private var startTime: Long = 0
    private var timerJob: Job? = null

    fun searchSounds(query: String) {
        _soundResults.value = ApiResult.Loading

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { repository.searchSound(query) }
            }

            result.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let {
                        val filterAudioFiles = filterAudioFiles(it.query?.search ?: emptyList())
                        _soundResults.value = Success(filterAudioFiles)
                    } ?: run {
                        _soundResults.value = ApiResult.Error("Empty response body")
                    }
                } else {
                    _soundResults.value =
                        ApiResult.Error("API Error: ${response.code()} ${response.message()}")
                }
            }.onFailure { e ->
                _soundResults.value =
                    ApiResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    // Similarly, you can create another function for fetching file details
    fun fetchFileDetails(fileName: String) {
        _fileDetails.value = ApiResult.Loading

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { repository.fetchFileDetails(fileName) }
            }
            result.onSuccess { response ->
                if (response?.isSuccessful!!) {
                    response.body()?.let { body ->
                        _fileDetails.value = Success(body)
                    } ?: run {
                        _fileDetails.value = ApiResult.Error("Empty response body")
                    }
                } else {
                    _fileDetails.value =
                        ApiResult.Error("API Error: ${response.code()} ${response.message()}")
                }
            }.onFailure { e ->
                _fileDetails.value =
                    ApiResult.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun downloadAndSaveFile(fileUrl: String, fileName: String) {
        viewModelScope.launch {


            val result = repository.downloadAndSaveFile(fileUrl, fileName)
            _downloadStatus.value = result
        }
    }

    fun insert(fileName: String, filePath: String) {
        viewModelScope.launch {
            repository.insert(Recording(fileName = fileName, filePath = filePath))
        }
    }

    fun deleteById(fileId: Int) = viewModelScope.launch {
        repository.deleteById(fileId)

    }


    fun setCurrentFile(soundDetails: SoundDetails) {
        currentFile.value = soundDetails
    }

    // Method to update selected sound details
    fun setSelectedSoundDetails(details: SelectedSoundDetails) {
        _selectedSoundDetails.value = details
    }

    fun clearSelectedSounds() {
        _selectedSoundDetails.value = null
    }

    fun setCheckBoxFlag(flag:Boolean = false){
        _setCheckBoxFlag.value = flag
    }

    fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                withContext(Dispatchers.Main) {
                    _elapsedTime.value = elapsed
                }
                delay(1000) // Update every second
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun getFormattedTime(): String {
        val elapsedTimeMillis = _elapsedTime.value ?: 0
        val seconds = (elapsedTimeMillis / 1000) % 60
        val minutes = (elapsedTimeMillis / 60000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Function to reset the timer
    fun resetTimer() {
        stopTimer()
        _elapsedTime.value = 0L
        startTimer()
    }

}





