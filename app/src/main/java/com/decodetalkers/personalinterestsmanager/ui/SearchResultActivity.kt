package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemArrayAdapter
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_BOOKS
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_MOVIES
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_MUSIC
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.NetworkViewModel
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultActivity : AppCompatActivity() {
    private lateinit var mediaType: String
    private lateinit var txtQuery: String
    private lateinit var gvAdapter: MediaItemArrayAdapter
    private lateinit var networkVM: NetworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        supportActionBar?.hide()
        networkVM =
            ViewModelProvider(this).get(NetworkViewModel::class.java)
        initViews()
    }

    private fun initViews(){
        txtQuery = intent.getStringExtra("query").toString()
        txt_query.text = txtQuery
        mediaType = intent.getStringExtra("mediaType").toString()
        initViewsWithType(mediaType)

        search_back.setOnClickListener {
            finish()
        }
    }

    private fun initViewsWithType(mediaType: String){
        when(mediaType){
            HEADER_MOVIES ->{
                searchForMovies(txtQuery)
            }
            HEADER_MUSIC ->{
                searchForMusic(txtQuery)
            }
            HEADER_BOOKS ->{

            }
        }
    }

    private fun searchForMusic(query: String){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            networkVM.getMusicSearchResults(query).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    gvAdapter = MediaItemArrayAdapter(::loadSongDetailsForActivity, this@SearchResultActivity, it)
                    results_gv.adapter = gvAdapter
                }
            }
        }
    }

    private fun searchForMovies(query: String){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            networkVM.getMoviesSearchResults(query).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    gvAdapter = MediaItemArrayAdapter(::loadMovieDetailsForActivity, this@SearchResultActivity, it)
                    results_gv.adapter = gvAdapter
                }
            }
        }
    }

    private fun loadMovieDetailsForActivity(movieId: String, movieImageView: ImageView, type: String = ""){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            networkVM.getMovieById(movieId).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    val intent = Intent(this@SearchResultActivity, MovieDetailActivity::class.java)
                    intent.putExtra("movie_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(this@SearchResultActivity, movieImageView, "SharedPoster")
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
    }

    private fun loadSongDetailsForActivity(songId: String, songImageView: ImageView, type:String = "") {
        if(type != MediaItemRecycler.TYPE_ARTIST) {
            UiManager().setProgressBarState(search_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                networkVM.getSongById(songId).collect {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(search_progress, false)
                        val intent = Intent(this@SearchResultActivity, SongDetailActivity::class.java)
                        intent.putExtra("song_model", it)
                        val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                            this@SearchResultActivity,
                            songImageView,
                            "SharedPoster"
                        )
                        startActivity(intent, actOptions.toBundle())
                    }
                }
            }
        }
    }

}