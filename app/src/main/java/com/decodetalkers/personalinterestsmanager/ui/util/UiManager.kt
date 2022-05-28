package com.decodetalkers.personalinterestsmanager.ui.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.widget.ProgressBar
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import java.util.*


class UiManager {

    fun setProgressBarState(progressBar: ProgressBar, active: Boolean){
        try {
            progressBar.visibility = if (active) View.VISIBLE else View.GONE

        }catch (e: Exception){

        }
    }

    fun setInitialTheme(activity: Context){
        val isDark = SharedPreferencesManager().isThemeDark()
        if(isDark){
            activity.setTheme(R.style.Theme_PersonalInterestsManagerDark)
        } else {
            activity.setTheme(R.style.Theme_PersonalInterestsManagerPrimary)
        }
    }

    fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}