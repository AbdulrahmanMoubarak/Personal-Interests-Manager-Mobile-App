package com.decodetalkers.personalinterestsmanager.ui

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel

import com.decodetalkers.personalinterestsmanager.models.MovieModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.MOVIE_IMAGE_LINK_L
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.MOVIE_IMAGE_LINK_M
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.TYPE_CAST_MEMBER
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.RatingDialogue
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.NetworkViewModel
import com.decodetalkers.radioalarm.application.MainApplication
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MovieDetailActivity : YouTubeBaseActivity() {
    private lateinit var mMovie: MovieModel

    private val mediaItemRecyclerAdapter = MediaItemRecycler(::loadMovieDetailsForActivity)
    private var sectionRecyclerAdapter = SectionRecycler(::loadMovieDetailsForActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        //supportActionBar?.hide()

        initViews()
    }

    private fun initViews() {
        mMovie = intent.getSerializableExtra("movie_model") as MovieModel
        movie_detail_title.text = mMovie.title
        movie_detail_desc.text = mMovie.overview
        movie_detail_status.text = mMovie.status
        movie_detail_releaseDate.text =
            if (mMovie.release_date != "None" && mMovie.release_date != "Unknown") mMovie.release_date else mMovie.status
        movie_detail_image.load(MOVIE_IMAGE_LINK_M + mMovie.poster)
        movie_detail_image.animation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
        movie_detail_background.load(MOVIE_IMAGE_LINK_L + mMovie.background) {
            crossfade(true)
            crossfade(300)
        }
        movie_detail_imagel_genres.text = mMovie.genres.replace("|", "  ")
        movie_detail_prod_comp.text = mMovie.production_company.replace("|", "  ")
        movie_detail_vote_average.text = mMovie.vote_average.toString()
        movie_detail_vote_count.text = mMovie.vote_count.toString()
        initYoutubePlayer(mMovie.trailer)
        loadMovieCast(mMovie.movie_id)
        loadMovieRecommendation(mMovie.movie_id)

        if(mMovie.user_rating != -1F){
            movieDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
            movie_detail_user_rating.visibility = View.VISIBLE
            movie_detail_user_rating.text = mMovie.user_rating.times(2).toString()
        } else {
            movieDetail_ButtonRating.setOnClickListener {
                showRatingDialogue()
            }
        }

    }

    private fun initYoutubePlayer(trailerId: String) {
        movie_detail_youtube_frame.initialize(
            "AIzaSyAQ0EfQEVuNuRn44SP6s6QTj-bU8WEDHuo",
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer, b: Boolean
                ) {
                    youTubePlayer.cueVideo(trailerId)
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

    private fun loadMovieCast(movieId: Int) {
        UiManager().setProgressBarState(movie_detail_cast_progress_bar, true)
        CoroutineScope(Dispatchers.IO).launch {
            getCastByMovieId(movieId).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(movie_detail_cast_progress_bar, false)
                    mediaItemRecyclerAdapter.setItem_List(it)
                    movie_detail_cast_recycler.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = mediaItemRecyclerAdapter
                    }
                }
            }
        }
    }

    private fun loadMovieRecommendation(movieId: Int) {
        UiManager().setProgressBarState(movie_detail_rec_progress_bar, true)
        CoroutineScope(Dispatchers.IO).launch {
            getMovieBasedRecommendation(movieId).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(movie_detail_rec_progress_bar, false)
                    sectionRecyclerAdapter.setItem_List(it)
                    movie_detail_rec_recycler.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        this.adapter = sectionRecyclerAdapter
                    }
                }
            }
        }
    }

    private fun loadMovieDetailsForActivity(
        movieId: String,
        movieImageView: ImageView,
        type: String = ""
    ) {
        if (type != TYPE_CAST_MEMBER) {
            CoroutineScope(Dispatchers.IO).launch {
                UiManager().setProgressBarState(movie_detail_rec_progress_bar, true)
                getMovieById(movieId).collect {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(movie_detail_rec_progress_bar, false)
                        val intent =
                            Intent(this@MovieDetailActivity, MovieDetailActivity::class.java)
                        intent.putExtra("movie_model", it)
                        val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                            this@MovieDetailActivity,
                            movieImageView,
                            "SharedPoster"
                        )
                        startActivity(intent, actOptions.toBundle())
                    }
                }
            }
        }
    }

    private fun getMovieById(movieId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieById(movieId, "2018170873").body() as MovieModel
        emit(response)
    }

    private fun getCastByMovieId(movieId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieCastById(movieId)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    private fun addMovieRating(userId: Int, movieId: Int, rating: Float) = flow {
        val response = RetrofitBuilder.pimApiService.addMovieRating(userId, movieId, rating)
        emit(response.code())
    }

    private fun getMovieBasedRecommendation(movieId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieBasedRecommendation(movieId)
            .body() as List<SectionModel>
        emit(response)
    }

    private fun showRatingDialogue() {
        val ratingDialogue = RatingDialogue(this, ::onRating)
        ratingDialogue.show()
    }

    private fun onRating(rating: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            addMovieRating(2018170873, mMovie.movie_id, rating).collect {
                Log.d("rating", "onRating: rating added")
                withContext(Dispatchers.Main) {
                    if(it == 200) {
                        Toast.makeText(this@MovieDetailActivity, "Rating Added", Toast.LENGTH_SHORT)
                            .show()
                        movieDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
                        movieDetail_ButtonRating.isClickable = false
                        movie_detail_user_rating.visibility = View.VISIBLE
                        movie_detail_user_rating.text = rating.times(2).toString()
                    } else {
                        Toast.makeText(this@MovieDetailActivity, "Error adding rating", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


}