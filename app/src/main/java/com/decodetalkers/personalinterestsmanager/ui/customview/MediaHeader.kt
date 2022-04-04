package com.decodetalkers.personalinterestsmanager.ui.customview

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.ui.SearchResultActivity
import com.decodetalkers.radioalarm.application.MainApplication
import kotlinx.android.synthetic.main.media_header.view.*


class MediaHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr)  {

    companion object{
        const val HEADER_MOVIES = "movies"
        const val HEADER_MUSIC = "music"
        const val HEADER_BOOKS = "books"
        const val HEADER_LIBRARY = "library"
    }

    private var viewItem: View
    private var headerImage: ImageView
    private var headerTitle: TextView
    private lateinit var mediaType: String
    private var isSearchView = false
    init {
        viewItem = inflate(getContext(), R.layout.media_header, this)
        headerImage = viewItem.imageView
        headerTitle = viewItem.txt_title

        header_searchIcon.setOnClickListener {
            toggleView()
        }

        search_backArrow.setOnClickListener {
            toggleView()
        }

        header_searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(context, SearchResultActivity::class.java).apply {
                    putExtra("mediaType", mediaType)
                    putExtra("query", query)
                }
                context.startActivity(intent)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    fun setHeaderType(headerType: String){
        mediaType = headerType
        when(headerType){
            HEADER_MOVIES -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.movies)
                headerImage.setImageResource(R.drawable.ic_movie_circle_svg)
            }
            HEADER_BOOKS ->{
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.books)
                headerImage.setImageResource(R.drawable.ic_book_circle_svgr)
            }
            HEADER_MUSIC ->{
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.music)
                headerImage.setImageResource(R.drawable.ic_music_circle_svg)
            }
            HEADER_LIBRARY ->{
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.library)
                headerImage.setImageResource(R.drawable.ic_library2)
            }
        }
    }

    fun toggleView(){
        if(!isSearchView){
            searchCard.visibility = View.VISIBLE
            titleCard.visibility = View.GONE
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            isSearchView = true
        } else{
            searchCard.visibility = View.GONE
            titleCard.visibility = View.VISIBLE
            val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(header_searchView.getWindowToken(), 0)
            isSearchView = false
        }
    }


}