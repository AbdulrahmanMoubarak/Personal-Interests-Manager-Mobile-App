package com.decodetalkers.personalinterestsmanager.ui.customview

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.radioalarm.application.MainApplication
import kotlinx.android.synthetic.main.create_playlist_dialog.*

class PlaylistDialogue(activity: Activity, var onSubmit: (name: String, type: String) -> Unit) :
    Dialog(activity), View.OnClickListener {

    private var type = "music"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.create_playlist_dialog)
        playlist_btnSubmit.setOnClickListener(this)
        playlist_btnCancel.setOnClickListener(this)
        movie_select.setOnClickListener(this)
        music_select.setOnClickListener(this)
        book_select.setOnClickListener(this)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.playlist_btnSubmit -> {
                if (pNameEditText.text.toString() == ""){
                    Toast.makeText(MainApplication.getAppContext(), context.getString(R.string.playlistNameError), Toast.LENGTH_SHORT).show()
                } else {
                    onSubmit(pNameEditText.text.toString(), this.type)
                    dismiss()
                }
            }
            R.id.playlist_btnCancel -> {
                dismiss()
            }
            R.id.movie_select -> {
                type = "movies"
                onIconClick(type)
            }
            R.id.music_select -> {
                type = "music"
                onIconClick(type)
            }
            R.id.book_select -> {
                type = "books"
                onIconClick(type)
            }
        }
    }

    private fun onIconClick(type:String){
        when(type){
            "movies" ->{
                movie_select.setImageResource(R.drawable.ic_movies2)
                music_select.setImageResource(R.drawable.ic_music_not_selected)
                book_select.setImageResource(R.drawable.ic_books_not_selected)
            }
            "music" ->{
                movie_select.setImageResource(R.drawable.ic_movies_not_selected)
                music_select.setImageResource(R.drawable.ic_music)
                book_select.setImageResource(R.drawable.ic_books_not_selected)
            }
            "books" ->{
                movie_select.setImageResource(R.drawable.ic_movies_not_selected)
                music_select.setImageResource(R.drawable.ic_music_not_selected)
                book_select.setImageResource(R.drawable.ic_books)
            }
        }
    }
}