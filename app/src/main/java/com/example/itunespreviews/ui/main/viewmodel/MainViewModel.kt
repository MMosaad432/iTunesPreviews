package com.example.itunespreviews.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itunespreviews.common.BaseViewModel
import com.example.itunespreviews.common.TracksRepository
import com.example.itunespreviews.models.Track
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {


    var tracksList = MutableLiveData<List<Track>>()
    var mPlaceHolderObserver = MutableLiveData<Boolean>()
    var mNavigateObserver = MutableLiveData<ArrayList<Track>>()
    var selectedPosition  = -1

    fun getRemoteTracks(term : String){
        viewModelScope.launch {
            TracksRepository.getTracks(term){
                mLoadingObserver.value = false
                tracksList.value  = it.tracks
                mPlaceHolderObserver.value = it.tracks.isEmpty()
            }
        }
    }

    fun getTrack(position: Int): Track? = tracksList.value?.get(position)
}