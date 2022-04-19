package com.decodetalkers.personalinterestsmanager.globalutils

import android.content.Context
import android.util.Log
import com.decodetalkers.radioalarm.application.MainApplication

class SharedPreferencesManager() {

    fun isLoaded(type: String): Boolean{
        val sp =
            MainApplication.getApplication().getSharedPreferences("onLogged", Context.MODE_PRIVATE)

        Log.d(SharedPreferencesManager::class.java.simpleName, "$type Loaded: ${sp.getBoolean(type, false)}")
        return sp.getBoolean(type, false)
    }

    fun setLoaded(type: String, status: Boolean){
        val sp =
            MainApplication.getApplication().getSharedPreferences("onLogged", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply {
            putBoolean(type, status)
            Log.d(SharedPreferencesManager::class.java.simpleName, "$type put Loaded: $status")
        }.apply()
    }
}