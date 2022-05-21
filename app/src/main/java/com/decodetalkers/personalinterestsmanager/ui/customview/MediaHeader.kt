package com.decodetalkers.personalinterestsmanager.ui.customview

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager
import com.decodetalkers.personalinterestsmanager.ui.SearchResultActivity
import com.decodetalkers.radioalarm.application.MainApplication
import com.google.android.gms.maps.model.LatLng
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
    private var headerBookstore: ImageView
    private var headerTitle: TextView
    private lateinit var mediaType: String
    private var isSearchView = false

    init {
        viewItem = inflate(getContext(), R.layout.media_header, this)
        headerImage = viewItem.imageView
        headerTitle = viewItem.txt_title
        headerBookstore = viewItem.bookstoresButton

        header_searchIcon.setOnClickListener {
            toggleView()
        }

        search_backArrow.setOnClickListener {
            toggleView()
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
                    val loc = getUserLocation()
                    if(loc.latitude == -1000000.0){
                        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
                        alertDialog.setTitle("Can't open maps")
                        alertDialog.setMessage("You have to enable location permission")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        alertDialog.show()
                    } else {
                        val gmmIntentUri =
                            Uri.parse("geo:${loc.latitude},${loc.longitude}?&q=nearby bookstores")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
                }
            }
            HEADER_MUSIC -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.music)
                headerImage.setImageResource(R.drawable.ic_music_circle_svg)
            }
            HEADER_LIBRARY -> {
                headerTitle.text = MainApplication.getAppContext()?.getText(R.string.library)
                headerImage.setImageResource(R.drawable.ic_library2)
            }
        }
    }

    fun toggleView() {
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
            if(location != null) {
                return LatLng(location.latitude, location.longitude)
            } else{
                return LatLng(-1000000.0, -1000000.0)
            }
        } else {
            return LatLng(-1000000.0, -1000000.0)
        }
    }
}