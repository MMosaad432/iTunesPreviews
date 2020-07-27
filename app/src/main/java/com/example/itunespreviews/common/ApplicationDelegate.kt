package com.example.itunespreviews.common

import android.app.Application
import com.example.itunespreviews.utilities.Api_Environment

class ApplicationDelegate : Application() {

    companion object{
        val environment = Api_Environment.development.getApiUrl()
    }
}