package com.example.recipebook.apis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObj {
    private const val BASE_URL = "https://api.edamam.com"
    val client= OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(15, TimeUnit.SECONDS)    // Read timeout
        .writeTimeout(15, TimeUnit.SECONDS)   // Write timeout
        .build()
    private val gson: Gson = GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .setLenient()
        .create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getInstance(): Retrofit {
        return retrofit
    }
}

