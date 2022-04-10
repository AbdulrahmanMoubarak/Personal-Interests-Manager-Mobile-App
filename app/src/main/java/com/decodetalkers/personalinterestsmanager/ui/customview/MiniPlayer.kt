package com.decodetalkers.personalinterestsmanager.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import com.decodetalkers.personalinterestsmanager.R
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.music_mini_player.view.*

class MiniPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var viewItem: View
    private var buttonPlayPause: ImageView
    private var buttonAddPlaylist: ImageView
    private var buttonReplay: ImageView
    private var isPlaying: Boolean
    private lateinit var youTubePlayer: YouTubePlayer

    init {
        viewItem = inflate(getContext(), R.layout.music_mini_player, this)
        buttonPlayPause = viewItem.mini_player_play_pause
        buttonAddPlaylist = viewItem.mini_player_addPlaylist
        buttonReplay = viewItem.mini_player_replay
        isPlaying = true

        buttonPlayPause.setOnClickListener {
            if(isPlaying){
                pause()
                isPlaying = false
            } else{
                play()
                isPlaying = true
            }
        }

        buttonReplay.setOnClickListener {
            youTubePlayer.seekToMillis(0)
        }

        mini_player_backward.setOnClickListener {
            youTubePlayer.seekRelativeMillis(-5000)
        }

        mini_player_forward.setOnClickListener {
            youTubePlayer.seekRelativeMillis(5000)
        }
    }

    fun subscribeYoutubePlayer(ytPlayer: YouTubePlayer){
        this.youTubePlayer = ytPlayer

        youTubePlayer.setPlaybackEventListener(
            object : YouTubePlayer.PlaybackEventListener{
                override fun onPlaying() {
                    buttonPlayPause.setImageResource(R.drawable.ic_pause_svgrepo_com)
                    isPlaying = true
                }

                override fun onPaused() {
                    buttonPlayPause.setImageResource(R.drawable.ic_play_svgrepo_com)
                    isPlaying = false
                }

                override fun onStopped() {
                    buttonPlayPause.setImageResource(R.drawable.ic_play_svgrepo_com)
                    isPlaying = false

                }

                override fun onBuffering(p0: Boolean) {

                }

                override fun onSeekTo(p0: Int) {

                }

            }
        )

        youTubePlayer.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener{
            override fun onLoading() {

            }

            override fun onLoaded(p0: String?) {

            }

            override fun onAdStarted() {

            }

            override fun onVideoStarted() {
            }

            override fun onVideoEnded() {
                youTubePlayer.seekToMillis(0)
            }

            override fun onError(p0: YouTubePlayer.ErrorReason?) {
            }

        })
    }

    private fun play(){
        if (!isPlaying &&youTubePlayer != null){
            buttonPlayPause.setImageResource(R.drawable.ic_pause_svgrepo_com)
            this.youTubePlayer.play()
        }
    }

    private fun pause(){
        if(isPlaying&&youTubePlayer != null){
            buttonPlayPause.setImageResource(R.drawable.ic_play_svgrepo_com)
            this.youTubePlayer.pause()
        }
    }
}