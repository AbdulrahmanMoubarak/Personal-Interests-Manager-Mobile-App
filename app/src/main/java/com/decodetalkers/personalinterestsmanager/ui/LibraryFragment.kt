package com.decodetalkers.personalinterestsmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.PlaylistAdapter
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.customview.PlaylistDialogue
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private lateinit var homeScreensVM: HomeScreensViewModel
    val recAdapter = PlaylistAdapter(::loadPlaylistItemsActivity)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleCardLibrary.setHeaderType(MediaHeader.HEADER_LIBRARY)

        homeScreensVM =
            ViewModelProvider(requireActivity()).get(HomeScreensViewModel::class.java)


        setRecyclerList(arrayListOf())

        loadAllPlaylists()

        createPlaylistButton.setOnClickListener {
            showPlaylistDialogue()
        }
        titleCardLibrary.setUserImage(AppUser.user_image)

    }

    private fun setRecyclerList(itemList: List<MediaItemOfListModel>) {
        recAdapter.setItem_List(itemList)
        playlists_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = recAdapter
        }
    }

    private fun onDialogueSubmit(name: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.createPlaylist(AppUser.user_id, name, type).collect {
                withContext(Dispatchers.Main) {
                    if (it) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.create_playlist_suc),
                            Toast.LENGTH_SHORT
                        ).show()
                        loadAllPlaylists()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.create_playlist_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadAllPlaylists(){
        UiManager().setProgressBarState(library_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getAllPlaylists(AppUser.user_id).collect{
                withContext(Dispatchers.Main){
                    try {
                        UiManager().setProgressBarState(library_progress, false)
                        setRecyclerList(it)
                    }catch (e: Exception){

                    }
                }
            }
        }
    }

    private fun showPlaylistDialogue() {
        val playlistDialogue = PlaylistDialogue(requireActivity(), ::onDialogueSubmit)
        playlistDialogue.show()
    }

    private fun loadPlaylistItemsActivity(plId: Int, name:String){
        val intent = Intent(requireContext(), PlaylistContentActivity::class.java)
        intent.putExtra("plId", plId)
        intent.putExtra("plName", name)
        startActivity(intent)
    }



}