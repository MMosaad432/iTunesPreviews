package com.example.itunespreviews.ui.player.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.itunespreviews.common.BaseViewModel
import com.example.itunespreviews.models.Track

class TrackPlayerViewModel : BaseViewModel() {
    var tracks = ArrayList<Track>()
    var position = -1

}