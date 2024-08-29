package com.lurenjia534.nextonedrive.ListItem

import okhttp3.logging.HttpLoggingInterceptor

object DriveRetrofitClient {
    private const val BASE_URL = "https://graph.microsoft.com/v1.0/"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val httpClient = okhttp3.OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: DriveApiService by lazy {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        retrofit.create(DriveApiService::class.java)
    }
}
