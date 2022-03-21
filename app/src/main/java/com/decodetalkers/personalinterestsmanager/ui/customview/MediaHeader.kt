package com.decodetalkers.personalinterestsmanager.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.decodetalkers.personalinterestsmanager.R
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
    init {
        viewItem = inflate(getContext(), R.layout.media_header, this)
        headerImage = viewItem.imageView
        headerTitle = viewItem.txt_title
    }

    fun setHeaderType(headerType: String){
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
}