package com.decodetalkers.personalinterestsmanager.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import coil.load
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.ArtistModel
import com.decodetalkers.personalinterestsmanager.models.SongModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.ArtistImageSliderAdapter
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.activity_song_detail.*

class SongDetailActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_detail)


        initViews()
    }

    private fun initViews() {
        val mSong = intent.getSerializableExtra("song_model") as SongModel
        song_detail_title.text = mSong.title
        song_detail_image.load(mSong.image)
        val songArtists = mSong.artists
        setBgRandomImage(songArtists, song_detail_bg_image)
        initYoutubePlayer(mSong.youtube_id)
        music_detail_yt_player.onFocusChangeListener = null
        setArtistNames(songArtists,song_detail_artists)
    }

    private fun initYoutubePlayer(songYtId: String){
        music_detail_yt_player.initialize(
            "AIzaSyAQ0EfQEVuNuRn44SP6s6QTj-bU8WEDHuo",
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer, b: Boolean
                ) {
                    youTubePlayer.cueVideo(songYtId)
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                    music_detail_miniplayer.subscribeYoutubePlayer(youTubePlayer)
                }
                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    Toast.makeText(applicationContext, "Video player Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }

    private fun setBgRandomImage(songArtists: List<ArtistModel>, bg: ImageView) {
        val list = arrayListOf<String>()
        for (artist in songArtists){
            list.add(artist.image)
            Log.d("here", "initViews: ${artist.image}")
        }
        val randomNum = (0..list.size-1).random()
        bg.load(list.get(randomNum))
    }

    private fun setArtistNames(songArtists: List<ArtistModel>, txtArtist: TextView){
        var str = ""
        for (artist in songArtists){
            str += artist.artist_name + " "
        }
        txtArtist.setText(str)
    }

}