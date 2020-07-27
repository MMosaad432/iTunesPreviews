package com.example.itunespreviews.common

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.itunespreviews.models.Track

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

open class BaseViewModel : ViewModel() {
    var mLoadingObserver = MutableLiveData<Boolean>()



    fun getFormattedDate(publishedAt: String?): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = format.parse(publishedAt ?: "")
        return DateFormat.format("dd,MMM yyyy", date) as String
    }
}