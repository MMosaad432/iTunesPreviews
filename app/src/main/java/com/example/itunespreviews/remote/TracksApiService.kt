package com.example.itunespreviews.remote

import com.example.itunespreviews.models.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApiService {
    @GET("search")
    fun getTracks(
        @Query("term") term : String
    ): Call<TracksResponse>
}