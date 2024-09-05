package com.example.soundmixer.features.search.data_models

import java.io.Serializable

data class SearchResponse(
    val batchcomplete: Boolean,
    val`continue`: Continue?,
    val warnings: Warnings?,
    val query: Query?
) : Serializable

data class Continue(
    val sroffset: Int?,
    val `continue`: String?
) : Serializable

data class Warnings(
    val main: MainWarning?,
    val search: SearchWarning?
) : Serializable

data class MainWarning(
    val warnings: String?
) : Serializable

data class SearchWarning(
    val warnings: String?
) : Serializable

data class Query(
    val search: List<SearchResult>?
) : Serializable

data class SearchResult(
    val ns: Int?,
    val title: String?,
    val pageid: Int?,
    val snippet: String?,
    val timestamp: String?
) : Serializable

