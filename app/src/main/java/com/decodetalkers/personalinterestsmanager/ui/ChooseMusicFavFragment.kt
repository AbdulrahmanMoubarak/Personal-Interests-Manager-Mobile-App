package com.decodetalkers.personalinterestsmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.withCreated
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.FavArtistAdapter
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.fragment_choose_music_fav.*
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseMusicFavFragment : Fragment() {
    val recAdapter = FavArtistAdapter(::onItemClick)
    val selectedItems = arrayListOf<String>()
    private lateinit var homeScreensVM: HomeScreensViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_music_fav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeScreensVM =
            ViewModelProvider(requireActivity()).get(HomeScreensViewModel::class.java)
        setRecyclerList(arrayListOf())
        loadTopArtists()

        artistProceedButton.setOnClickListener {
            if(selectedItems.size < 3){
                Toast.makeText(requireContext(), getString(R.string.select3Artists), Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putStringArrayList("artistList", selectedItems)
                }
                Navigation.findNavController(view).navigate(R.id.action_chooseMusicFavFragment_to_chooseMusicGenresFragment, bundle)
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

    private fun setRecyclerList(itemList: List<MediaItemOfListModel>) {
        recAdapter.setItem_List(itemList)
        topArtistRecycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = recAdapter
        }
    }

    private fun loadTopArtists() {
        UiManager().setProgressBarState(musiv_fav_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getTopMusicArtists().collect {
                withContext(Dispatchers.Main){
                    UiManager().setProgressBarState(musiv_fav_progress, false)
                    setRecyclerList(it)
                }
            }
        }
    }
}