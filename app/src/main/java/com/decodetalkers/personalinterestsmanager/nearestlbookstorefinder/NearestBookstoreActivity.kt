package com.decodetalkers.personalinterestsmanager.nearestlbookstorefinder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.databinding.ActivityNearestBookstoreBinding
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager

class NearestBookstoreActivity : AppCompatActivity(),OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityNearestBookstoreBinding
    private lateinit var userLocarion: LatLng
    lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNearestBookstoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment


        if (PermissionManager().checkForPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            getUserLocation()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0, 0.0)))
    }

    private fun setupMap() {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            mMap = it
            mMap.clear()
            mMap.setMinZoomPreference(10f)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocarion))
            searchForBookStores()
        }
    }

    private fun searchForBookStores() {
        val geocoder = Geocoder(this)

        val addressList = geocoder.getFromLocationName("105 Abou Bakr El-Sedeek, Almazah, Heliopolis, Cairo Governorate", 5)
        for (address in addressList) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(address.latitude, address.longitude))
                    .title(address.getAddressLine(0))
                    .icon(BitmapFromVector(this, R.drawable.ic_bookstore_map_marker))
            )

            mMap.setOnInfoWindowClickListener { marker ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=${marker.position.latitude},${marker.position.longitude}")
                )
                startActivity(intent)
            }
        }
    }


    private fun getUserLocation() {
        if (PermissionManager().checkForPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {
                userLocarion = LatLng(it.latitude, it.longitude)
                setupMap()
            }
        }
    }



    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        bitmap.width = 5
        bitmap.height = 5

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}