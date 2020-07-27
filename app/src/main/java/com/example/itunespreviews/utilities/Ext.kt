package com.example.itunespreviews.utilities

import android.content.Intent
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.itunespreviews.R

fun ImageView.loadImage(url :String?){
    Glide
        .with(context)
        .load(url)
        .placeholder(R.drawable.default_content)
        .into(this)
}

