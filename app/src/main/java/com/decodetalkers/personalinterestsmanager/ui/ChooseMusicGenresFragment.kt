package com.decodetalkers.personalinterestsmanager.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.GenreModel
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.FavArtistAdapter
import com.decodetalkers.personalinterestsmanager.ui.adapters.FavGenresAdapter
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.fragment_choose_music_fav.*
import kotlinx.android.synthetic.main.fragment_choose_music_genres_fav.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseMusicGenresFragment : Fragment() {
    val recAdapter = FavGenresAdapter(::onItemClick)
    val selectedItems = arrayListOf<String>()
    var selectedArtists = arrayListOf<String>()
    private lateinit var homeScreensVM: HomeScreensViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        selectedArtists = arguments?.getStringArrayList("artistList") as ArrayList<String>
        return inflater.inflate(R.layout.fragment_choose_music_genres_fav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeScreensVM =
            ViewModelProvider(requireActivity()).get(HomeScreensViewModel::class.java)
        setRecyclerList(arrayListOf())
        loadGenres()

        genresProceedButton.setOnClickListener {
            if (selectedItems.size < 3) {
                Toast.makeText(
                    requireContext(),
                    "You have to select at least 3 genres",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                registerPrefs()
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
        genresRecycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = recAdapter
        }
    }

    private fun loadGenres() {
        UiManager().setProgressBarState(musiv_fav_gen_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getMusicGenres().collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(musiv_fav_gen_progress, false)
                    setRecyclerList(it)
                }
            }
        }
    }

    private fun registerPrefs() {
        var genresStr = ""
        var artistsStr = ""

        for (genre in selectedItems) {
            genresStr += genre + ","
        }

        for (artist in selectedArtists) {
            artistsStr += artist + ","
        }

        UiManager().setProgressBarState(musiv_fav_gen_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.registerUserPrefs(AppUser.user_email, artistsStr, genresStr).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(musiv_fav_gen_progress, false)
                    if (!it) {
                        Toast.makeText(
                            requireContext(),
                            "Error, Please Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        SharedPreferencesManager().setUserLogin(
                            AppUser.user_id,
                            AppUser.user_name,
                            AppUser.user_email,
                            "Complete"
                        )
                        Intent(requireContext(), HomeActivity::class.java).apply {
                            startActivity(this)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }
}