package com.example.soundmixer.network

import com.example.soundmixer.features.search.data_models.FileDetailsResponse
import com.example.soundmixer.features.search.data_models.SearchResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("w/api.php")
    suspend fun searchSounds(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String,
        @Query("srnamespace") namespace: Int = 6,
        @Query("format") format: String = "json",
        @Query("srlimit") limit: Int = 20
    ): Response<SearchResponse>

    @GET("w/api.php")
    suspend fun getFileDetails(
        @Query("action") action: String = "query",
        @Query("prop") prop: String = "imageinfo",
        @Query("titles") titles: String,
        @Query("iiprop") iiprop: String = "url",
        @Query("format") format: String = "json"
    ): Response<FileDetailsResponse>

    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>

}