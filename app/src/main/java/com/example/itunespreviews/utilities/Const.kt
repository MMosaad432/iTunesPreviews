package com.example.itunespreviews.utilities

enum class Api_Environment  {

    development {
        override fun getApiUrl(): String = "https://itunes.apple.com/"
    },

    prouction {
        override fun getApiUrl(): String  = ""
    };

    abstract fun getApiUrl() : String
}