package com.decodetalkers.personalinterestsmanager.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitYoutubeApi {
    @GET
    suspend fun getYoutubeSearchResults (
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("key") key: String = "AIzaSyAQ0EfQEVuNuRn44SP6s6QTj-bU8WEDHuo",
    )
}