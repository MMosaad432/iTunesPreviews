package com.example.itunespreviews.ui.player.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.itunespreviews.R
import com.example.itunespreviews.ui.player.viewmodel.TrackPlayerViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import java.io.IOException


class TrackPlayerActivity : AppCompatActivity() {


    companion object{
        lateinit var mediaSession: MediaSessionCompat
    }
    private lateinit var userAgent: String
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    lateinit var trackPlayerViewModel: TrackPlayerViewModel
    var mExoPlayer: SimpleExoPlayer? = null
    lateinit var mPlayerView: PlayerView
    lateinit var mediaSource: ProgressiveMediaSource
    lateinit var trackSelector: DefaultTrackSelector

    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mNotificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_player)
        setupViewModel()
        trackPlayerViewModel.tracks = intent.getParcelableArrayListExtra("track") ?: arrayListOf()
        trackPlayerViewModel.position = intent.getIntExtra("position", -1)
        mPlayerView = findViewById(R.id.playerControlView)

        initializeMediaSession()

        initializeImage()

        initializePlayer()


    }


    private fun setupViewModel() {
        trackPlayerViewModel = ViewModelProvider(this).get(TrackPlayerViewModel::class.java)
    }

    private fun initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            trackSelector = DefaultTrackSelector(this)
            mExoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
            mPlayerView.player = mExoPlayer
            mPlayerView.requestFocus()
            // Prepare the MediaSource.
            userAgent =
                Util.getUserAgent(this, "iTunesPreviews")
            val pos = trackPlayerViewModel.position
            trackPlayerViewModel.position = 0
            addItems()
            mExoPlayer?.prepare(concatenatingMediaSource, false, false)
            mExoPlayer?.seekTo(pos, C.TIME_UNSET)
            mExoPlayer?.playWhenReady = true
        }
        mExoPlayer?.addAnalyticsListener(EventLogger(trackSelector))
        mExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer!!.currentPosition, 1f
                    )
                } else if (playbackState == ExoPlayer.STATE_READY) {

                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer!!.currentPosition, 1f
                    )
                }

                mediaSession.setPlaybackState(stateBuilder.build())
                showNotification(stateBuilder.build())
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    // Active playback.
                } else {
                    // Not playing because playback is paused, ended, suppressed, or the player
                    // is buffering, stopped or failed. Check player.getPlaybackState,
                    // player.getPlayWhenReady, player.getPlaybackError and
                    // player.getPlaybackSuppressionReason for details.
                }
            }

            /**
             * When a failure occurs, this method will be called immediately before the playback state transitions to Player.STATE_IDLE.
             * Failed or stopped playbacks can be retried by calling ExoPlayer.retry.**/
            override fun onPlayerError(error: ExoPlaybackException) {
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    val cause: IOException = error.sourceException
                    if (cause is HttpDataSourceException) {
                        // An HTTP error occurred.
                        val httpError = cause
                        // This is the request for which the error occurred.
                        val requestDataSpec = httpError.dataSpec

                        if (httpError is InvalidResponseCodeException) {
                            // Cast to InvalidResponseCodeException and retrieve the response code,
                            // message and headers.
                        } else {
                            // Try calling httpError.getCause() to retrieve the underlying cause,
                            // although note that it may be null.
                        }
                    }
                }
            }
        })
    }

    private fun initializeImage() {
        Glide.with(this)
            .asDrawable()
            .load(trackPlayerViewModel.tracks[trackPlayerViewModel.position].artworkUrl100)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    drawable: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    mPlayerView.defaultArtwork = drawable
                }

            })
    }

    private fun releasePlayer() {
        mExoPlayer?.stop()
        mExoPlayer?.release()
        mExoPlayer = null
    }

    private fun addItems() {

        var mediaSources: ArrayList<MediaSource> = arrayListOf()
        var list: List<MediaSource>
        for (track in trackPlayerViewModel.tracks) {
            mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(
                    this, userAgent
                )
            )
                .createMediaSource(Uri.parse(trackPlayerViewModel.tracks[trackPlayerViewModel.position].previewUrl))
            mediaSources.add(mediaSource)
            trackPlayerViewModel.position += 1
        }
        list = mediaSources.toList()
        concatenatingMediaSource = ConcatenatingMediaSource(*list.toTypedArray())
    }

    private fun initializeMediaSession() {

        // Create a MediaSessionCompat.
        mediaSession =
            MediaSessionCompat(this, "mediaSessionTag")

        // Enable callbacks from MediaButtons and TransportControls.
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.setMediaButtonReceiver(null)

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
            )

        mediaSession.setPlaybackState(stateBuilder.build())


        // MySessionCallback has methods that handle callbacks from a media controller.
        mediaSession.setCallback(MySessionCallback())

        // Start the Media Session since the activity is active.
        mediaSession.isActive = true
    }

    private fun showNotification(state: PlaybackStateCompat) {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(
                "CHANNEL_1",
                "channel iTunes",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "CHANNEL_1")
        val icon: Int
        val playPause: String
        if (state.state == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause
            playPause = "Pause"
        } else {
            icon = R.drawable.exo_controls_play
            playPause = "Play"
        }
        val playPauseAction: NotificationCompat.Action = NotificationCompat.Action(
            icon, playPause,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
        )
        val restartAction: NotificationCompat.Action = NotificationCompat.Action(
            R.drawable.exo_controls_previous, "Restart",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )
        val nextAction: NotificationCompat.Action = NotificationCompat.Action(
            R.drawable.exo_controls_next, "Next",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                this,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )

        builder.setContentTitle("Adele")
            .setContentText(
                "Rolling in the deep"
            )
            .setSmallIcon(R.drawable.ic_music_note)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(restartAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(true)

        mNotificationManager.notify(0, builder.build())
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    fun makeNotificationChannel(id: String, name: String, importance: Int) {
        val channel = NotificationChannel(id, name, importance)
        mNotificationManager.createNotificationChannel(channel)
    }


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private inner class MySessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            mExoPlayer?.playWhenReady = true
        }

        override fun onPause() {
            mExoPlayer?.playWhenReady = false
        }

        override fun onSkipToNext() {
            mExoPlayer?.next()
        }
        override fun onSkipToPrevious() {
            mExoPlayer?.seekTo(0)
        }
    }

    class MediaReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            MediaButtonReceiver.handleIntent(
                mediaSession,
                intent
            )
        }
    }
}