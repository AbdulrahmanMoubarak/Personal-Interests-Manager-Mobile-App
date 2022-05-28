package com.decodetalkers.personalinterestsmanager.ui

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
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_playlist_content.*
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class PlaylistContentActivity : AppCompatActivity() , ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private var plId: Int = -1
    private var plName: String = ""
    private lateinit var homeScreensVM: HomeScreensViewModel
    private lateinit var recAdapter: MediaItemArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_playlist_content)
        supportActionBar?.hide()

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        checkAndSetLanguage()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)

        plId = intent.getIntExtra("plId", -1)
        plName = intent.getStringExtra("plName").toString()
        palylist_name.text = plName
        loadPlaylistItems()

    }

    fun loadPlaylistItems(){
        UiManager().setProgressBarState(content_progress, true)
        CoroutineScope(Dispatchers.IO).launch{
            homeScreensVM.loadPlaylistItems(plId).collect{
                withContext(Dispatchers.Main){
                    UiManager().setProgressBarState(content_progress, false)
                    recAdapter = MediaItemArrayAdapter(
                        ::initViewsWithType, this@PlaylistContentActivity,
                        it
                    )
                    playlist_items_recycler.adapter = recAdapter
                }
            }
        }
    }


    private fun initViewsWithType(id: String, image: ImageView, mediaType: String) {
        when (mediaType) {
            MediaHeader.HEADER_MOVIES -> {
                loadMovieDetailsForActivity(id, mediaType)
            }
            MediaHeader.HEADER_MUSIC -> {
                loadSongDetailsForActivity(id, mediaType)
            }
            MediaHeader.HEADER_BOOKS -> {
                loadBookDetailsForActivity(id, mediaType)
            }
        }
    }

    private fun loadMovieDetailsForActivity(movieId: String, type: String = "") {
        UiManager().setProgressBarState(content_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getMovieById(movieId).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(content_progress, false)
                    val intent =
                        Intent(this@PlaylistContentActivity, MovieDetailActivity::class.java)
                    intent.putExtra("movie_model", it)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadSongDetailsForActivity(songId: String, type: String = "") {
        if (type != MediaItemRecycler.TYPE_ARTIST) {
            UiManager().setProgressBarState(content_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                homeScreensVM.getSongById(songId).collect {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(content_progress, false)
                        val intent =
                            Intent(this@PlaylistContentActivity, SongDetailActivity::class.java)
                        intent.putExtra("song_model", it)

                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun loadBookDetailsForActivity(
        bookId: String,
        type: String = ""
    ) {
        UiManager().setProgressBarState(content_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getBookById(bookId, AppUser.user_id).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(content_progress, false)
                    val intent =
                        Intent(this@PlaylistContentActivity, BookDetailActivity::class.java)
                    intent.putExtra("book_model", it)
                    startActivity(intent)
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