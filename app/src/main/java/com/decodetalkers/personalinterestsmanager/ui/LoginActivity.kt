package com.decodetalkers.personalinterestsmanager.ui

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.UserModel
import com.decodetalkers.personalinterestsmanager.ui.util.FormValidator
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.util.*

class LoginActivity : AppCompatActivity(), ActivityInterface {
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private lateinit var homeScreensVM: HomeScreensViewModel

    private var isLoadingBooksFinished = false
    private var isLoadingMoviesFinished = false
    private var isLoadingMusicFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        checkAndSetLanguage()

        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)

        if (!PermissionManager().checkForPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            PermissionManager().requestPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        loginButton.setOnClickListener {
            if (FormValidator().validateLogin(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            ) {
                validateEmailRemotely()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "You have to enter proper data",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        registerButton.setOnClickListener {
            if (FormValidator().validateRegisteration(
                    emailRegEditText.text.toString(),
                    usernameRegEditText.text.toString(),
                    passwordRegEditText.text.toString(),
                    passwordConfirmRegEditText.text.toString()
                )
            ) {
                registerUserRemotely()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "You have to enter proper data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        registerFrame.setOnClickListener {
            registerView.visibility = View.VISIBLE
            afterAnimationView.visibility = View.GONE

        }
        loginFrame.setOnClickListener {
            afterAnimationView.visibility = View.VISIBLE
            registerView.visibility = View.GONE

        }

        loadSomething()
    }

    override fun onStart() {
        super.onStart()
        if (!SharedPreferencesManager().isThemeDark())
            bookIconImageView.setImageResource(R.drawable.ic_logo_h_v2_blue)
        else
            bookIconImageView.setImageResource(R.drawable.ic_logo_h_v2_orange)
    }

    private fun validateEmailRemotely() {
        UiManager().setProgressBarState(login_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.login(
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
            ).collect {
                try {
                    val user = it as UserModel
                    AppUser.setUserData(user.user_id.toInt(), user.user_name, user.user_email)
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(login_progress, false)
                        SharedPreferencesManager().setUserLogin(
                            AppUser.user_id,
                            AppUser.user_name,
                            AppUser.user_email,
                            "Complete"
                        )
                        Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(login_progress, false)
                        Toast.makeText(this@LoginActivity, "Invalid Data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun registerUserRemotely() {
        UiManager().setProgressBarState(login_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.registerUser(
                emailRegEditText.text.toString(),
                usernameRegEditText.text.toString(),
                passwordRegEditText.text.toString(),
            ).collect {
                if (it == 200) {
                    homeScreensVM.login(
                        emailRegEditText.text.toString(),
                        passwordRegEditText.text.toString(),
                    ).collect {
                        val user = it as UserModel
                        AppUser.setUserData(user.user_id.toInt(), user.user_name, user.user_email)
                        withContext(Dispatchers.Main) {
                            UiManager().setProgressBarState(login_progress, false)
                            SharedPreferencesManager().setUserLogin(
                                AppUser.user_id,
                                AppUser.user_name,
                                AppUser.user_email,
                                "notComplete"
                            )
                            Intent(this@LoginActivity, SelectFavouritesActivity::class.java).apply {
                                startActivity(this)
                                finish()
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(login_progress, false)
                        Toast.makeText(
                            this@LoginActivity,
                            "Email already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadSomething() {
        CoroutineScope(Dispatchers.IO).launch {
            fakeLoad().collect {
                isLoadingMoviesFinished = true
                if (checkAllLoadingFinished()) {
                    withContext(Dispatchers.Main) {
                        onLoadingFinished()
                    }
                }
            }
        }
    }

    private fun fakeLoad() = flow {
        delay(2000)
        emit(true)
    }

    private fun checkAllLoadingFinished(): Boolean {
        return (isLoadingMoviesFinished)
    }

    private fun onLoadingFinished() {
        loadingProgressBar.visibility = View.GONE
        if (!SharedPreferencesManager().isThemeDark())
            bookIconImageView.setImageResource(R.drawable.ic_logo_h_v2_blue)
        else
            bookIconImageView.setImageResource(R.drawable.ic_logo_h_v2_orange)
        if(SharedPreferencesManager().isUserLoggedIn()){
            val user = SharedPreferencesManager().getUser()
            AppUser.setUserData(user.user_id.toInt(), user.user_name, user.user_email)
            if(SharedPreferencesManager().isUserStateComplete()) {
                Intent(this@LoginActivity, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                Intent(this@LoginActivity, SelectFavouritesActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        } else {
            startAnimation()
        }
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

    private fun checkAndSetLanguage(){
        if(SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
            UiManager().setLocale(this, "ar")
        }
        else {
            setLanguage(Locale.ENGLISH)
            UiManager().setLocale(this, "en")
        }
    }

}