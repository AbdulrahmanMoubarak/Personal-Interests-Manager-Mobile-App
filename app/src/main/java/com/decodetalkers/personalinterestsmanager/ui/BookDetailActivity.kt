package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.BookModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.ChoosePlaylistDialog
import com.decodetalkers.personalinterestsmanager.ui.customview.RatingDialogue
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BookDetailActivity : AppCompatActivity() , ActivityInterface {
    private lateinit var mBook: BookModel
    private var sectionRecyclerAdapter = SectionRecycler(::loadBookDetailsForActivity)
    private lateinit var homeScreensVM: HomeScreensViewModel
    private val localizationDelegate = LocalizationActivityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_book_detail)
        supportActionBar?.hide()

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
        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)
        mBook = intent.getSerializableExtra("book_model") as BookModel
        book_detail_title.text = mBook.book_title
        book_detail_authors.text = mBook.book_author
        book_detail_desc.text = mBook.description
        book_detail_cat.text = mBook.categories.replace(',', ' ')
        book_detail_image.load(mBook.image_url)

        if (mBook.user_rating != -1F) {
            bookDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
            book_detail_user_rating.visibility = View.VISIBLE
            book_detail_user_rating.text = mBook.user_rating.times(2).toString()
        } else {
            bookDetail_ButtonRating.setOnClickListener {
                showRatingDialogue()
            }
        }

        movieDetail_ButtonGBooks.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(mBook.preview_link)
            startActivity(i)
        }

        bookDetail_ButtonAddPl.setOnClickListener {
            showPlaylistDialog()
        }

        loadRecommendations()
    }

    private fun showRatingDialogue() {
        val ratingDialogue = RatingDialogue(this, ::onRating)
        ratingDialogue.show()
    }

    private fun loadRecommendations() {
        CoroutineScope(Dispatchers.IO).launch {
            UiManager().setProgressBarState(book_detail_rec_progress_bar, true)
            homeScreensVM.getBookBasedRecommendation(mBook.isbn).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(book_detail_rec_progress_bar, false)
                    setRecyclerList(it)
                }
            }
        }
    }

    private fun setRecyclerList(list: List<SectionModel>) {
        sectionRecyclerAdapter.setItem_List(list)
        book_detail_rec_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = sectionRecyclerAdapter
        }
    }

    private fun loadBookDetailsForActivity(
        bookId: String,
        bookImageView: ImageView,
        type: String = ""
    ) {
        UiManager().setProgressBarState(book_detail_rec_progress_bar, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getBookById(bookId, AppUser.user_id).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(book_detail_rec_progress_bar, false)
                    val intent = Intent(this@BookDetailActivity, BookDetailActivity::class.java)
                    intent.putExtra("book_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                        this@BookDetailActivity,
                        bookImageView,
                        "SharedPoster"
                    )
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
    }

    private fun onRating(rating: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.addBookRating(AppUser.user_id, mBook.isbn, mBook.book_title, rating * 2)
                .collect {
                    withContext(Dispatchers.Main) {
                        if (it == 200) {
                            Toast.makeText(
                                this@BookDetailActivity,
                                getString(R.string.ratingAdded),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            bookDetail_ButtonRating.setImageResource(R.drawable.ic_user_rating)
                            bookDetail_ButtonRating.isClickable = false
                            book_detail_user_rating.visibility = View.VISIBLE
                            book_detail_user_rating.text = rating.times(2).toString()
                        } else {
                            Toast.makeText(
                                this@BookDetailActivity,
                                getString(R.string.ratingError),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
        }
    }

    private fun addBookToPlaylist(playlistId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.addPlaylistItem(playlistId, mBook.isbn, mBook.book_title, mBook.image_url)
                .collect {
                    withContext(Dispatchers.Main) {
                        if (it) {
                            Toast.makeText(
                                this@BookDetailActivity,
                                "${getString(R.string.successAdd)} ${mBook.book_title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@BookDetailActivity,
                                "${getString(R.string.failedAdd)} ${mBook.book_title}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    private fun showPlaylistDialog() {
        val plDialog = ChoosePlaylistDialog(this, ::addBookToPlaylist, "books")
        plDialog.show()
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
}