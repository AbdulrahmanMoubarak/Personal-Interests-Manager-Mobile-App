package com.decodetalkers.personalinterestsmanager.retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


object RetrofitBuilder {

    private const val PERSONAL_INTERESTS_MANAGER_SERVER_URL = "http://192.168.1.7:5001/"
    //private const val PERSONAL_INTERESTS_MANAGER_SERVER_URL = "https://cc59-41-129-61-49.ngrok.io"

    private fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(getClient())
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()))
            .build() //Doesn't require the adapter
    }

    val pimApiService: RetrofitPimApi = getRetrofit(PERSONAL_INTERESTS_MANAGER_SERVER_URL).create()

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(24L, TimeUnit.SECONDS)
            .connectTimeout(24L, TimeUnit.SECONDS)
            .build();
    }
}