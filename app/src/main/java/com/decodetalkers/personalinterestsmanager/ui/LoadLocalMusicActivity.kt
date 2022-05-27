package com.decodetalkers.personalinterestsmanager.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.myapplication.locatmusicloader.util.Constants.EXTERNAL_STORAGE_ROOT_PATH
import com.decodetalkers.myapplication.locatmusicloader.util.Constants.INTERNAL_STORAGE_ROOT_PATH
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.PermissionManager
import com.decodetalkers.personalinterestsmanager.locatmusicloader.LocalMusicLoader
import com.decodetalkers.personalinterestsmanager.models.GenreModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.FavGenresAdapter
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.activity_load_local_music.*
import kotlinx.android.synthetic.main.fragment_choose_music_genres_fav.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LoadLocalMusicActivity : AppCompatActivity() {
    val recAdapter = FavGenresAdapter(::onItemClick, true)
    private lateinit var homeScreensVM: HomeScreensViewModel
    val recList = arrayListOf<GenreModel>()
    val selectedItems = arrayListOf<String>()
    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d("here", "openDirectory: ${data?.dataString}")
                if (data != null) {
                    result?.data?.also { uri ->
                        searchLocalMusic(uri)
                    }
                } else {
                    finish()
                }
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UiManager().setInitialTheme(this)
        setContentView(R.layout.activity_load_local_music)
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= 23) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getColor(R.color.black)
        }


        homeScreensVM =
            ViewModelProvider(this).get(HomeScreensViewModel::class.java)


        if (PermissionManager().checkForPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openDirectory(Uri.parse(INTERNAL_STORAGE_ROOT_PATH))
        } else {
            if (PermissionManager().requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                openDirectory(Uri.parse(INTERNAL_STORAGE_ROOT_PATH))
            } else {
                finish()
            }
        }

        localMusicProceedButton.setOnClickListener {
            uploadMusicData()
        }
    }

    private fun uploadMusicData(){
        showLoadingLayout("Uploading Music Data")
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.uploadLocalMusicData(AppUser.user_id, selectedItems).collect {
                withContext(Dispatchers.Main) {
                    if (it) {
                        Toast.makeText(
                            this@LoadLocalMusicActivity,
                            "Successfully uploaded music data",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    } else {
                        Toast.makeText(
                            this@LoadLocalMusicActivity,
                            "Error uploading music data, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()
                    }
                }
            }
        }
    }

    fun openDirectory(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
        }
        resultLauncher.launch(intent)
    }

    private fun searchLocalMusic(uri: Intent) {
        val mUri: Uri? = uri.data
        val x = File(mUri?.path)
        val z = x.absolutePath.split(":")
        val dirPath = z[1]
        val dir = File("${INTERNAL_STORAGE_ROOT_PATH}/${dirPath}")
        showLoadingLayout("Loading music from ${dir.name}")
        CoroutineScope(Dispatchers.IO).launch {
            LocalMusicLoader().loadAllLocalMusicPaths(dir.absolutePath).collect {
                withContext(Dispatchers.Main) {
                    showResultLayout()
                    for (file in it) {
                        val filename = file.name.split('.')[0].split('(')[0].split('[')[0]
                        selectedItems.add(filename)
                        recList.add(GenreModel(filename).apply { selected = true })
                    }
                    setRecyclerList(recList)
                }
            }
        }
    }

    private fun onItemClick(itemId: String) {
        if (itemId !in selectedItems) {
            selectedItems.add(itemId)
        } else {
            selectedItems.remove(itemId)
        }
    }

    private fun setRecyclerList(itemList: List<GenreModel>) {
        recAdapter.setItem_List(itemList)
        localMusicRecycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = recAdapter
        }
    }

    private fun showLoadingLayout(msg: String) {
        resultLayout.visibility = View.GONE
        loadingLayout.visibility = View.VISIBLE
        txt_operation.text = msg
    }

    private fun showResultLayout() {
        resultLayout.visibility = View.VISIBLE
        loadingLayout.visibility = View.GONE
    }
}