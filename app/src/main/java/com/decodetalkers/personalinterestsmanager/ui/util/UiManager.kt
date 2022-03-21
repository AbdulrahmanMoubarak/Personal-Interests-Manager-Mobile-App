package com.decodetalkers.personalinterestsmanager.ui.util

import android.view.View
import android.widget.ProgressBar

class UiManager {

    fun setProgressBarState(progressBar: ProgressBar, active: Boolean){
        try {
            progressBar.visibility = if (active) View.VISIBLE else View.GONE

        }catch (e: Exception){

        }
    }
}