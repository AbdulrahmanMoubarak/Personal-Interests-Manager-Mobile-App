package com.decodetalkers.personalinterestsmanager.ui

import android.animation.Animator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class LoginActivity : AppCompatActivity() {

    private lateinit var homeScreensVM: HomeScreensViewModel

    private var isLoadingBooksFinished = false
    private var isLoadingMoviesFinished = false
    private var isLoadingMusicFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        AppUser.setUserData(2018170873, "AbdulRahman Moubarak", "oddaled@gmail.com")

        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)

        loginButton.setOnClickListener {
            Intent(this, HomeActivity::class.java).apply {
                finish()
                startActivity(this)
            }
        }
        loadSomething()
    }

    override fun onStart() {
        super.onStart()

    }

    private fun loadSomething() {
        CoroutineScope(Dispatchers.IO).launch {
            fakeLoad().collect {
                isLoadingMoviesFinished = true
                if(checkAllLoadingFinished()){
                    withContext(Dispatchers.Main){
                        onLoadingFinished()
                    }
                }
            }
        }
    }

    private fun fakeLoad() = flow{
        delay(2000)
        emit(true)
    }

    private fun checkAllLoadingFinished(): Boolean{
        return (isLoadingMoviesFinished)
    }

    private fun onLoadingFinished() {
        loadingProgressBar.visibility = View.GONE
        bookIconImageView.setImageResource(R.drawable.ic_logo_h_v2)
        startAnimation()
    }

    private fun startAnimation() {
        bookIconImageView.animate().apply {
            scaleXBy(0.3f)
            scaleYBy(0.3f)
            translationYBy(-600f)
            duration = 300
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                afterAnimationView.visibility = VISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })


    }
}