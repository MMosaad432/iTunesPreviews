package com.example.itunespreviews.common

import com.example.itunespreviews.models.TracksResponse
import com.example.itunespreviews.remote.RetrofitClient
import com.example.itunespreviews.remote.TracksApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

object TracksRepository {

    private val retrofitApi = RetrofitClient.getClient().create(TracksApiService::class.java)

    suspend fun getTracks(term : String,onComplete : (TracksResponse) -> Unit)  = withContext(Dispatchers.IO){
        val tracks = retrofitApi.getTracks(term).await()
        withContext(Dispatchers.Main){
            onComplete(tracks)
        }
    }
}