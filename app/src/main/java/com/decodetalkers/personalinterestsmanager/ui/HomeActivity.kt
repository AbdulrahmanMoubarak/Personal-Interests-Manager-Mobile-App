package com.decodetalkers.personalinterestsmanager.ui

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity() , ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UiManager().setInitialTheme(this)
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        checkAndSetLanguage()

        homeBottomNavigationMenu.itemIconTintList = null

        var controller = findNavController(R.id.homeNavView)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.moviesFragment,
                R.id.booksFragment,
                R.id.musicFragment,
                R.id.libraryFragment
            )
        )


        setupActionBarWithNavController(controller, appBarConfiguration)

        homeBottomNavigationMenu.setupWithNavController(controller)

        chatbotButton.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
    }

    override fun onStart() {
        super.onStart()
        if (!PermissionManager().checkForPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
            PermissionManager().requestPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.BOOKS, false)
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.MOVIES, false)
        SharedPreferencesManager().setLoaded(HomeScreensViewModel.MUSIC, false)
    }


    private fun checkAndSetLanguage(){
        if(SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
        }
        else {
            setLanguage(Locale.ENGLISH)
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