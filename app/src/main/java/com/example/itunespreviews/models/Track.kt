package com.example.itunespreviews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track (
    val artistName: String,
    val trackName: String,
    val previewUrl: String,
    val artworkUrl100: String,
    val releaseDate: String,
    val primaryGenreName: String
) : Parcelable