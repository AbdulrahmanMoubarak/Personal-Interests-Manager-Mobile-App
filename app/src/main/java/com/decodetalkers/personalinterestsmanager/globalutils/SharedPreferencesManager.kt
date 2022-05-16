package com.decodetalkers.personalinterestsmanager.globalutils

import android.content.Context
import android.util.Log
import com.decodetalkers.personalinterestsmanager.models.UserModel
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

    fun setUserLogin(userId: Int, username: String, email:String, state:String){
        val sp =
            MainApplication.getApplication().getSharedPreferences("AppUser", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply {
            putBoolean("logged", true)
            putInt("userId", userId)
            putString("userName", username)
            putString("userEmail", email)
            putString("userState", state)
        }.apply()
    }

    fun isUserLoggedIn():Boolean{
        val sp =
            MainApplication.getApplication().getSharedPreferences("AppUser", Context.MODE_PRIVATE)
        return sp.getBoolean("logged", false)
    }

    fun isUserStateComplete():Boolean{
        val sp =
            MainApplication.getApplication().getSharedPreferences("AppUser", Context.MODE_PRIVATE)
        val state =  sp.getString("userState", "")
        return state=="Complete"
    }

    fun getUser():UserModel{
        val sp =
            MainApplication.getApplication().getSharedPreferences("AppUser", Context.MODE_PRIVATE)
        val userId = sp.getInt("userId", -1)
        val username = sp.getString("userName", "")
        val email = sp.getString("userEmail", "")
        return UserModel(userId.toString(), username.toString(), email.toString())
    }
}