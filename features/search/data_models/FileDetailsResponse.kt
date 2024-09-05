package com.example.soundmixer.features.search.data_models

data class FileDetailsResponse(
    val batchcomplete: String?,
    val query: Query
) {
    data class Query(
        val pages: Map<String, Page>
    ) {
        data class Page(
            val pageid: Int,
            val ns: Int,
            val title: String,
            val imagerepository: String,
            val imageinfo: List<ImageInfo>
        ) {
            data class ImageInfo(
                val url: String,
                val descriptionurl: String,
                val descriptionshorturl: String
            )
        }
    }
}

