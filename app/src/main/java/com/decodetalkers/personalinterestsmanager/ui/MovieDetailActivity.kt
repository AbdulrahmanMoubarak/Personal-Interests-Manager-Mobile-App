package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel

import com.decodetalkers.personalinterestsmanager.models.MovieModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.MOVIE_IMAGE_LINK_L
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.MOVIE_IMAGE_LINK_M
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.TYPE_CAST_MEMBER
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.ChoosePlaylistDialog
import com.decodetalkers.personalinterestsmanager.ui.customview.RatingDialogue
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MovieDetailActivity : YouTubeBaseActivity(), ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private lateinit var mMovie: MovieModel

    private val mediaItemRecyclerAdapter = MediaItemRecycler(::loadMovieDetailsForActivity)
    private var sectionRecyclerAdapter = SectionRecycler(::loadMovieDetailsForActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_movie_detail)

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        checkAndSetLanguage()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        initViews()
    }

    private fun initViews() {
        mMovie = intent.getSerializableExtra("movie_model") as MovieModel
        movie_detail_title.text = mMovie.title
        movie_detail_desc.text = mMovie.overview
        movie_detail_status.text =
            if (mMovie.status == "Released") getString(R.string.released)
            else if (mMovie.status == "Post Production") getString(R.string.postproduction)
            else if (mMovie.status == "Planned") getString(R.string.planned) else ""
        movie_detail_releaseDate.text =
            if (mMovie.release_date != "None" && mMovie.release_date != "Unknown") mMovie.release_date else mMovie.status
        movie_detail_image.animation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
        movie_detail_image.load(MOVIE_IMAGE_LINK_M + mMovie.poster)
        if (mMovie.background != "") {
            movie_detail_background.load(MOVIE_IMAGE_LINK_L + mMovie.background) {
                crossfade(true)
                crossfade(300)
            }
        } else {
            movie_detail_background.visibility = View.GONE
            movie_detail_background_grad.visibility = View.GONE
        }

        if (mMovie.genres == "")
            genres_card.visibility = View.GONE
        else
            movie_detail_imagel_genres.text = mMovie.genres.replace("|", "  ")
        if (mMovie.production_company == "")
            movie_detail_prod_comp.text = getString(R.string.unknown)
        else
            movie_detail_prod_comp.text = mMovie.production_company.replace("|", "  ")
        movie_detail_vote_average.text = mMovie.vote_average.toString()
        movie_detail_vote_count.text = mMovie.vote_count.toString()

        if (mMovie.trailer == "")
            trailer_card.visibility = View.GONE
        else
            initYoutubePlayer(mMovie.trailer)
        loadMovieCast(mMovie.movie_id)
        loadMovieRecommendation(mMovie.movie_id)

        btnImdb.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://www.imdb.com/title/${mMovie.imdb_id}/")
            startActivity(i)
        }

        btnTmdb.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://www.themoviedb.org/movie/${mMovie.movie_id}/")
            startActivity(i)
        }

        if (mMovie.user_rating != -1F) {
            movieDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
            movie_detail_user_rating.visibility = View.VISIBLE
            movie_detail_user_rating.text = mMovie.user_rating.times(2).toString()
        } else {
            movieDetail_ButtonRating.setOnClickListener {
                showRatingDialogue()
            }
        }

        movie_detail_add_pl.setOnClickListener {
            showPlaylistDialog()
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
            getMovieBasedRecommendation(movieId, AppUser.user_id).collect {
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
        val response =
            RetrofitBuilder.pimApiService.getMovieById(movieId.toInt(), AppUser.user_id.toString())
                .body() as MovieModel
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

    private fun getMovieBasedRecommendation(movieId: Int, userId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieBasedRecommendation(movieId, userId)
            .body() as List<SectionModel>
        emit(response)
    }

    private fun showRatingDialogue() {
        val ratingDialogue = RatingDialogue(this, ::onRating)
        ratingDialogue.show()
    }

    private fun onRating(rating: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            addMovieRating(AppUser.user_id, mMovie.movie_id, rating).collect {
                Log.d("rating", "onRating: rating added")
                withContext(Dispatchers.Main) {
                    if (it == 200) {
                        Toast.makeText(
                            this@MovieDetailActivity,
                            getString(R.string.ratingAdded),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        movieDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
                        movieDetail_ButtonRating.isClickable = false
                        movie_detail_user_rating.visibility = View.VISIBLE
                        movie_detail_user_rating.text = rating.times(2).toString()
                    } else {
                        Toast.makeText(
                            this@MovieDetailActivity,
                            getString(R.string.ratingError),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }
    }

    private fun getAddMovieToPlaylistResult(playlistId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            addMovieToPlaylist(playlistId).collect {
                withContext(Dispatchers.Main) {
                    if (it) {
                        Toast.makeText(
                            this@MovieDetailActivity,
                            "${getString(R.string.successAdd)} ${mMovie.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MovieDetailActivity,
                            "${getString(R.string.failedAdd)} ${mMovie.title}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun addMovieToPlaylist(playlistId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.addPlaylistItem(
            playlistId,
            mMovie.movie_id.toString(),
            mMovie.title,
            MOVIE_IMAGE_LINK_M + mMovie.poster
        )
        if (response.code() == 200) {
            emit(true)
        } else {
            emit(false)
        }
    }

    private fun showPlaylistDialog() {
        val plDialog = ChoosePlaylistDialog(this, ::getAddMovieToPlaylistResult, "movies")
        plDialog.show()
    }

    public override fun onResume() {
        super.onResume()
        localizationDelegate.onResume(this)
    }

    override fun attachBaseContext(newBase: Context) {
        applyOverrideConfiguration(localizationDelegate.updateConfigurationLocale(newBase))
        super.attachBaseContext(newBase)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(super.getResources())
    }

    override fun setLanguage(language: String?) {
        localizationDelegate.setLanguage(this, language!!)
    }

    override fun setLanguage(locale: Locale?) {
        localizationDelegate.setLanguage(this, locale!!)
    }

    override fun getCurrentLocale(): Locale {
        return localizationDelegate.getLanguage(this)
    }

    val currentLanguage: Locale
        get() = localizationDelegate.getLanguage(this)

    override fun onAfterLocaleChanged() {
    }

    override fun onBeforeLocaleChanged() {
    }

    private fun checkAndSetLanguage() {
        if (SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
            UiManager().setLocale(this, "ar")
        } else {
            setLanguage(Locale.ENGLISH)
            UiManager().setLocale(this, "en")
        }
    }


}