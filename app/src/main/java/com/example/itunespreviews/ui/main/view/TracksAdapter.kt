package com.example.itunespreviews.ui.main.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.itunespreviews.R
import com.example.itunespreviews.models.Track
import com.example.itunespreviews.ui.main.viewmodel.MainViewModel
import com.example.itunespreviews.ui.player.view.TrackPlayerActivity
import com.example.itunespreviews.utilities.loadImage

class TracksAdapter(
    val mViewModel: MainViewModel
) :
    RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {

        return TracksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tracks_list, parent, false)
        )
    }

    override fun getItemCount(): Int = mViewModel.tracksList.value?.count() ?: 0

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = mViewModel.getTrack(position)!!

        holder.trackContainer.setOnClickListener {
            mViewModel.selectedPosition = position
            mViewModel.mNavigateObserver.value = mViewModel.tracksList.value as ArrayList<Track>
        }
        holder.artWorkImageView.loadImage(track.artworkUrl100)
        holder.genreTextView.text = track.primaryGenreName
        holder.releaseDateTextView.text = mViewModel.getFormattedDate(track.releaseDate)
        holder.trackNameTextView.text = track.trackName
        holder.artistNameTextView.text  = track.artistName



    }


    inner class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val trackContainer : ConstraintLayout = itemView.findViewById(R.id.trackContainer)
        val artWorkImageView: ImageView = itemView.findViewById(R.id.artWorkImageView)
        val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
        val releaseDateTextView: TextView = itemView.findViewById(R.id.releaseDateTextView)
        val trackNameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)

    }


}