package com.example.weathertracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val api: JsonPlaceholderApi by lazy {
        createRetrofitApi()
    }

    private fun createRetrofitApi(): JsonPlaceholderApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonPlaceholderApi::class.java)
    }
}
