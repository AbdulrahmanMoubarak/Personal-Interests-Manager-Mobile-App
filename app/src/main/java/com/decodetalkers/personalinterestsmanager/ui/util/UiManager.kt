package com.decodetalkers.personalinterestsmanager.ui.util

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager

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
}