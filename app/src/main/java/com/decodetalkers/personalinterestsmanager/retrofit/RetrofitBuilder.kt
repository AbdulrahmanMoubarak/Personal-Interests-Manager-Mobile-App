package com.decodetalkers.personalinterestsmanager.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitBuilder {

    private const val PERSONAL_INTERESTS_MANAGER_SERVER_URL = "http://192.168.1.4:5001/"

    private fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()))
            .build() //Doesn't require the adapter
    }
    val pimApiService: RetrofitPimApi = getRetrofit(PERSONAL_INTERESTS_MANAGER_SERVER_URL).create()
}