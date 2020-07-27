package com.example.itunespreviews.common

import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.itunespreviews.models.Track
import com.example.itunespreviews.ui.player.view.TrackPlayerActivity


abstract class BaseActivity : AppCompatActivity() {
    abstract var progressbarId: Int?

    private var loadingProgressBar: ProgressBar? = null

    override fun onStart() {
        loadingProgressBar = progressbarId?.let { findViewById(it) }
        super.onStart()
    }

    private fun navigate(destination :  Class<*>){
        val intent = Intent(this,destination)
        startActivity(intent)
    }

    fun navigate(destination :  Class<*>,track : ArrayList<Track>, selectedPosition : Int){
        val intent = Intent(this,destination)
        intent.putExtra("track",track)
        intent.putExtra("position",selectedPosition)
        startActivity(intent)
    }

    open fun registerObserver(viewModel: BaseViewModel){
        viewModel.mLoadingObserver.observe(this, Observer {
            if (it)
                loadingProgressBar?.visibility = View.VISIBLE
            else
                loadingProgressBar?.visibility = View.GONE
        })


    }
}