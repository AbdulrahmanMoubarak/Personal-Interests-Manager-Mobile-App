package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemArrayAdapter
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemAdapter
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_BOOKS
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_MOVIES
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader.Companion.HEADER_MUSIC
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SearchResultActivity : AppCompatActivity() , ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private lateinit var mediaType: String
    private lateinit var txtQuery: String
    private lateinit var gvAdapter: MediaItemArrayAdapter
    private lateinit var homeScreensVM: HomeScreensViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_search_result)
        supportActionBar?.hide()

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        checkAndSetLanguage()

        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)
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
                searchForBooks(txtQuery)
            }
        }
    }

    private fun searchForMusic(query: String){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getMusicSearchResults(query).collect{
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
            homeScreensVM.getMoviesSearchResults(query).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    gvAdapter = MediaItemArrayAdapter(::loadMovieDetailsForActivity, this@SearchResultActivity, it)
                    results_gv.adapter = gvAdapter
                }
            }
        }
    }

    private fun searchForBooks(query: String){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getBooksSearchResults(query).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    gvAdapter = MediaItemArrayAdapter(::loadBookDetailsForActivity, this@SearchResultActivity, it)
                    results_gv.adapter = gvAdapter
                }
            }
        }
    }

    private fun loadMovieDetailsForActivity(movieId: String, movieImageView: ImageView, type: String = ""){
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getMovieById(movieId).collect{
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
        if(type != MediaItemAdapter.TYPE_ARTIST) {
            UiManager().setProgressBarState(search_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                homeScreensVM.getSongById(songId).collect {
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

    private fun loadBookDetailsForActivity(
        bookId: String,
        bookImageView: ImageView,
        type: String = ""
    ) {
        UiManager().setProgressBarState(search_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getBookById(bookId, AppUser.user_id).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(search_progress, false)
                    val intent = Intent(this@SearchResultActivity, BookDetailActivity::class.java)
                    intent.putExtra("book_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(this@SearchResultActivity, bookImageView, "SharedPoster")
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
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

    private fun checkAndSetLanguage(){
        if(SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
            UiManager().setLocale(this, "ar")
        }
        else {
            setLanguage(Locale.ENGLISH)
            UiManager().setLocale(this, "en")
        }
    }

}