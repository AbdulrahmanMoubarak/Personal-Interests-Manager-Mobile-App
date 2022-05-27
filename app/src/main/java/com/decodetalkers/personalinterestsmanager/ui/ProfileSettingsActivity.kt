package com.decodetalkers.personalinterestsmanager.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import kotlinx.android.synthetic.main.activity_profile_settings.*
import java.io.File


class ProfileSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_profile_settings)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        initViews()

        darkThemeSwitch.setOnCheckedChangeListener { _, b ->
            if (b){
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

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun initViews(){
        userProfileName.text = AppUser.user_name
        if(AppUser.user_image != ""){
            setUserImage()
        }
        darkThemeSwitch.isChecked = SharedPreferencesManager().isThemeDark()
        languageSwitch.isChecked = SharedPreferencesManager().getLang() == "ar"
    }

    private fun setUserImage(){
        Glide.with(this)
            .load(Uri.parse(AppUser.user_image))
            .circleCrop()
            .into(userProfileImage)
    }

    private fun logout(){
        SharedPreferencesManager().logoutUser()
        val i = Intent(this, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
}