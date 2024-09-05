package com.example.soundmixer.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.soundmixer.repository.SoundRepository
import com.example.soundmixer.viewmodels.SoundViewModel

class SoundViewModelFactory(
    private val soundRepository: SoundRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SoundViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SoundViewModel(soundRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}