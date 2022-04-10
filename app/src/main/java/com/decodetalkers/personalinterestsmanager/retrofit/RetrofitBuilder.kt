package com.decodetalkers.personalinterestsmanager.retrofit
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


object RetrofitBuilder {

    private const val PERSONAL_INTERESTS_MANAGER_SERVER_URL_LOCAL = "http://192.168.1.6:5001/"
    private const val PERSONAL_INTERESTS_MANAGER_SERVER_URL_GLOBAL = "https://cc34-45-245-25-32.ngrok.io/"

    private fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()))
            .build()
    }
    val pimApiService: RetrofitPimApi = getRetrofit(PERSONAL_INTERESTS_MANAGER_SERVER_URL_LOCAL).create()


}