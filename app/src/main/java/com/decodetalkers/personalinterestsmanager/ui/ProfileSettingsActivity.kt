package com.decodetalkers.personalinterestsmanager.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import kotlinx.android.synthetic.main.activity_profile_settings.*
import java.util.*


class ProfileSettingsActivity : AppCompatActivity(), ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private lateinit var userImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_profile_settings)
        supportActionBar?.hide()

        userImage = findViewById(R.id.userProfileImage)
        initViews(AppUser.user_image)

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        checkAndSetLanguage()

        darkThemeSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                setTheme(R.style.Theme_PersonalInterestsManagerDark)
                SharedPreferencesManager().setTheme(true)
            } else {
                setTheme(R.style.Theme_PersonalInterestsManagerPrimary)
                SharedPreferencesManager().setTheme(false)
            }
            SharedPreferencesManager().setTheme(b)
            val i = Intent(this, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        languageSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                setLanguage("ar")
                SharedPreferencesManager().setLang("ar")
            } else {
                setLanguage(Locale.ENGLISH)
                SharedPreferencesManager().setLang("en")
            }
            val i = Intent(this, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    override fun onStart() {
        super.onStart()
        setUserImage(AppUser.user_image)
    }

    private fun initViews(image: String) {
        userProfileName.text = AppUser.user_name
        darkThemeSwitch.isChecked = SharedPreferencesManager().isThemeDark()
        languageSwitch.isChecked = SharedPreferencesManager().getLang() == "ar"
        setUserImage(image)

    }

    private fun setUserImage(image:String) {
        Log.d(ProfileSettingsActivity::class.java.simpleName, "setUserImage: ${image}")
//        Glide.with(this)
//            .load(Uri.parse(image))
//            .circleCrop()
//            .into(userImage)
//            .onLoadFailed(getDrawable(R.drawable.ic_baseline_account_circle_24))
        userImage.load(Uri.parse(image)){
            transformations(CircleCropTransformation())
        }
    }

    private fun logout() {
        SharedPreferencesManager().logoutUser()
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
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

    private fun checkAndSetLanguage() {
        if (SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
        } else {
            setLanguage(Locale.ENGLISH)
        }
    }

}