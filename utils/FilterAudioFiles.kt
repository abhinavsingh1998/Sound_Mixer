package com.example.soundmixer.utils

import com.example.soundmixer.features.search.data_models.SearchResult

fun filterAudioFiles(results: List<SearchResult>): List<SearchResult> {
    return results.filter { result ->
        result.title?.let { title ->
            title.endsWith(".ogg") || title.endsWith(".webm") || title.endsWith(".wav")
        } ?: false

    }
}

