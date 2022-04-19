package com.decodetalkers.personalinterestsmanager.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }


        homeBottomNavigationMenu.itemIconTintList = null

        var controller = findNavController(R.id.homeNavView)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.moviesFragment, R.id.booksFragment, R.id.musicFragment, R.id.libraryFragment))

        setupActionBarWithNavController(controller, appBarConfiguration)

        homeBottomNavigationMenu.setupWithNavController(controller)

        chatbotButton.imageTintList= ColorStateList.valueOf(Color.rgb(255, 255, 255))
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.BOOKS, false)
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.MOVIES, false)
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.MUSIC, false)
    }
}