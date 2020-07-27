package com.example.itunespreviews.remote

import com.example.itunespreviews.common.ApplicationDelegate
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {

        return if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(ApplicationDelegate.environment)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit!!
        } else {
            retrofit!!
        }

    }
}