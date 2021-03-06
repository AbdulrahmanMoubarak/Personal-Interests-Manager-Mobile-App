package com.decodetalkers.personalinterestsmanager.ui

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.ChatMessageModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.ChatAdapter
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_chatbot.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChatbotActivity : AppCompatActivity(), ActivityInterface {
    private lateinit var homeScreensVM: HomeScreensViewModel
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private val recAdapter = ChatAdapter()
    private val curMsgId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_chatbot)
        supportActionBar?.hide()
        checkAndSetLanguage()
        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }

        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)

        setupMessageRecycler()

        chat_send.setOnClickListener {
            if (msgEditText.text.toString() != "") {
                (chatRecycler.adapter as ChatAdapter).addNewMessage(
                    ChatMessageModel(
                        curMsgId + 1,
                        msgEditText.text.toString(),
                        false
                    )
                )

                CoroutineScope(Dispatchers.IO).launch {
                    homeScreensVM.getChatbotReply(curMsgId + 1, msgEditText.text.toString())
                        .collect {
                            withContext(Dispatchers.Main) {
                                (chatRecycler.adapter as ChatAdapter).addNewMessage(it)
                            }
                        }
                }
                msgEditText.text?.clear()
            }
        }
    }

    private fun setupMessageRecycler() {
        chatRecycler.adapter = recAdapter
    }

    private fun checkAndSetLanguage() {
        if (SharedPreferencesManager().getLang() == "ar") {
            setLanguage("ar")
        } else {
            setLanguage(Locale.ENGLISH)
        }
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
}