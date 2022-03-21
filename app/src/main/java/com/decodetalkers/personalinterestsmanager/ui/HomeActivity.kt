package com.decodetalkers.personalinterestsmanager.ui

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.viewmodels.NetworkViewModel
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()


        homeBottomNavigationMenu.itemIconTintList = null

        var controller = findNavController(R.id.homeNavView)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.moviesFragment, R.id.booksFragment, R.id.musicFragment, R.id.libraryFragment))

        setupActionBarWithNavController(controller, appBarConfiguration)

        homeBottomNavigationMenu.setupWithNavController(controller)

        chatbotButton.imageTintList= ColorStateList.valueOf(Color.rgb(255, 255, 255))
    }
}