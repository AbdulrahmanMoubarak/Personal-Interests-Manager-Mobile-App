package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.ArtistModel
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.models.SongModel
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.ui.adapters.ArtistImageSliderAdapter
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.TYPE_ARTIST
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.activity_song_detail.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.lang.Exception

class SongDetailActivity : YouTubeBaseActivity() {

    private lateinit var mSong: SongModel
    private var sectionRecyclerAdapter = SectionRecycler(::loadSongDetailsForActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_detail)

        initViews()

    }

    private fun initViews() {
        mSong = intent.getSerializableExtra("song_model") as SongModel
        song_detail_title.text = mSong.title
        song_detail_image.load(mSong.image)
        initYoutubePlayer(mSong.youtube_id)
        loadSongRecommendation(mSong.song_spotify_id)
        music_detail_yt_player.onFocusChangeListener = null
    }

    private fun initYoutubePlayer(songYtId: String) {
        music_detail_yt_player.initialize(
            "AIzaSyAQ0EfQEVuNuRn44SP6s6QTj-bU8WEDHuo",
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer, b: Boolean
                ) {
                    youTubePlayer.loadVideo(songYtId)
                    youTubePlayer.play()
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                    music_detail_miniplayer.subscribeYoutubePlayer(youTubePlayer)
                    try {
                        getSongListeningTimesUpdateResult(500)
                    }catch (e: Exception){
                        Log.d(SongDetailActivity::class.java.simpleName, "updateListening: Failed")
                    }
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

    private fun getArtistsSection(): List<SectionModel> {
        val artistList: ArrayList<MediaItemOfListModel> = arrayListOf()
        for (artist in mSong.artists) {
            artistList.add(
                MediaItemOfListModel(
                    artist.artist_spotify_id,
                    artist.artist_name,
                    artist.image,
                    TYPE_ARTIST
                )
            )
        }
        return arrayListOf(SectionModel("Song Artists", artistList))
    }

    private fun loadSongRecommendation(songId: String) {
        UiManager().setProgressBarState(song_detail_rec_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            getSongRecommendation(songId).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(song_detail_rec_progress, false)
                    sectionRecyclerAdapter.setItem_List(getArtistsSection() + it)

                    song_detail_rec_recycler.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        adapter = sectionRecyclerAdapter
                    }
                }
            }
        }
    }

    private fun loadSongDetailsForActivity(
        songId: String,
        songImageView: ImageView,
        type: String = ""
    ) {
        if (type != TYPE_ARTIST) {
            UiManager().setProgressBarState(song_detail_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                getSongById(songId).collect {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(song_detail_progress, false)
                        val intent = Intent(this@SongDetailActivity, SongDetailActivity::class.java)
                        intent.putExtra("song_model", it)
                        val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                            this@SongDetailActivity,
                            songImageView,
                            "SharedPoster"
                        )
                        startActivity(intent, actOptions.toBundle())
                    }
                }
            }
        }
    }

    private fun getSongById(songId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getSongById(songId).body() as SongModel
        emit(response)
    }

    private fun getSongRecommendation(songId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getSongBasedRecommendation(songId)
            .body() as List<SectionModel>
        emit(response)
    }

    private fun updateUserSongListening(songId: String, userId: Int) = flow{
        val response = RetrofitBuilder.pimApiService.updateSongListeningTimes(userId, songId)
        emit(response.code())
    }

    private fun getSongListeningTimesUpdateResult(blockTime: Long){
        CoroutineScope(Dispatchers.IO).launch {
            updateUserSongListening(mSong.song_spotify_id, 2018170873).collect {
                if (it != 200){
                    delay(blockTime)
                    if (blockTime < 20000){
                        getSongListeningTimesUpdateResult(blockTime*2)
                    }
                }
            }
        }
    }

}