package com.example.itunespreviews.models

import com.google.gson.annotations.SerializedName

data class TracksResponse (
    val resultCount: Long,
    @SerializedName("results")
    val tracks: List<Track>
)