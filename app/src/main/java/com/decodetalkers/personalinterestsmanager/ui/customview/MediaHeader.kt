package com.decodetalkers.personalinterestsmanager.ui.customview

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager
import com.decodetalkers.personalinterestsmanager.ui.LoadLocalMusicActivity
import com.decodetalkers.personalinterestsmanager.ui.ProfileSettingsActivity
import com.decodetalkers.personalinterestsmanager.ui.SearchResultActivity
import com.decodetalkers.radioalarm.application.MainApplication
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_profile_settings.*
import kotlinx.android.synthetic.main.media_header.view.*


class MediaHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        const val HEADER_MOVIES = "movies"
        const val HEADER_MUSIC = "music"
        const val HEADER_BOOKS = "books"
        const val HEADER_LIBRARY = "library"
    }

    private var viewItem: View
    private var headerImage: ImageView
    private var headerProfileImage: ImageView
    private var headerBookstore: ImageView
    private var headerLocalMusic: ImageView
    private var headerTitle: TextView
    private lateinit var mediaType: String
    private var isSearchView = false



    init {
        viewItem = inflate(getContext(), R.layout.media_header, this)
        headerImage = viewItem.imageView
        headerProfileImage = viewItem.header_profile
        headerTitle = viewItem.txt_title
        headerBookstore = viewItem.bookstoresButton
        headerLocalMusic = viewItem.localMusicButton

        setUserImage()


        header_searchIcon.setOnClickListener {
            toggleView()
        }

        search_backArrow.setOnClickListener {
            toggleView()
        }

        header_profile.setOnClickListener {
            val intent = Intent(context, ProfileSettingsActivity::class.java)
            context.startActivity(intent)
        }

        header_searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

    fun setHeaderType(headerType: String) {
        mediaType = headerType
        when (headerType) {
            HEADER_MOVIES -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.movies)
                headerImage.setImageResource(R.drawable.ic_movie_circle_svg)
            }
            HEADER_BOOKS -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.books)
                headerImage.setImageResource(R.drawable.ic_book_circle_svgr)
                headerBookstore.visibility = View.VISIBLE
                headerBookstore.setOnClickListener {
                    findNearestBookstores()
                }
            }
            HEADER_MUSIC -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.music)
                headerImage.setImageResource(R.drawable.ic_music_circle_svg)
                headerLocalMusic.visibility = View.VISIBLE
                headerLocalMusic.setOnClickListener {
                    openLocalMusicActivity()
                }

            }
            HEADER_LIBRARY -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.library)
                headerImage.setImageResource(R.drawable.ic_library2)
            }
        }
    }

    private fun findNearestBookstores() {
        val loc = getUserLocation()
        if (loc.latitude == -1000000.0) {
            val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
            alertDialog.setTitle(context.getString(R.string.mapError))
            alertDialog.setMessage(context.getString(R.string.enableLocation))
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL,
                context.getString(R.string.ok),
                { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        } else {
            val gmmIntentUri =
                Uri.parse("geo:${loc.latitude},${loc.longitude}?&q=nearby bookstores")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    }

    private fun openLocalMusicActivity() {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(context.getString(R.string.loadLocalMusic))
        alertDialog.setMessage(context.getString(R.string.loadLocalMusicMsg))
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.yes)
        ) { dialog, _ ->
            val intent = Intent(context, LoadLocalMusicActivity::class.java)
            context.startActivity(intent)
            dialog.dismiss()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.no)
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun toggleView() {
        if (!isSearchView) {
            searchCard.visibility = View.VISIBLE
            titleCard.visibility = View.GONE
            header_searchView.requestFocus()
            val imm: InputMethodManager? =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            isSearchView = true
        } else {
            searchCard.visibility = View.GONE
            titleCard.visibility = View.VISIBLE
            val imm = context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(header_searchView.getWindowToken(), 0)
            isSearchView = false
        }
    }

    private fun getUserLocation(): LatLng {
        if (PermissionManager().checkForPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                return LatLng(location.latitude, location.longitude)
            } else {
                return LatLng(-1000000.0, -1000000.0)
            }
        } else {
            return LatLng(-1000000.0, -1000000.0)
        }
    }

    fun setUserImage(){
        Glide.with(MainApplication.getAppContext())
            .load(AppUser.user_image)
            .circleCrop()
            .into(headerProfileImage)
            .onLoadFailed(context.getDrawable(R.drawable.ic_baseline_account_circle_24))
    }

    fun setUserImage(image:String){
        Glide.with(this)
            .load(Uri.parse(image))
            .circleCrop()
            .into(headerProfileImage)
            .onLoadFailed(context.getDrawable(R.drawable.ic_baseline_account_circle_24))
    }
}