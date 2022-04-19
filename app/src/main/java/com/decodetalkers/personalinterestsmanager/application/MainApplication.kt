package com.decodetalkers.radioalarm.application

import android.app.Application
import android.content.Context
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel

class MainApplication: Application() {

    companion object{
        private lateinit var app: Application
        fun getAppContext(): Context {
            return app.applicationContext
        }

        fun getApplication(): Application {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}