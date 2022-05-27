package com.decodetalkers.personalinterestsmanager.ui.customview

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.ui.adapters.PlaylistAdapter
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import kotlinx.android.synthetic.main.choose_playlist_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChoosePlaylistDialog(
    var activity: Activity,
    var onSubmit: (id: Int) -> Unit,
    var type: String
) :
    Dialog(activity), View.OnClickListener {
    val recAdapter = PlaylistAdapter(::loadPlaylistItemsActivity)

    private fun createPlaylist(userId: Int, playlistName: String, type: String) = flow {
        val response = RetrofitBuilder.pimApiService.createPlaylist(userId, playlistName, type)
        if (response.code() == 200) {
            emit(true)
        } else {
            emit(false)
        }
    }

    private fun loadPlaylists(userId: Int, type: String) = flow{
        val response = RetrofitBuilder.pimApiService.getAllPlayListsOfType(userId, type)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    private fun loadPlaylistItemsActivity(id: Int, name: String) {
        onSubmit(id)
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.choose_playlist_dialog)
        choosePl_createButton.setOnClickListener(this)

        loadAllPlaylists()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.choosePl_createButton -> {
                showPlaylistDialogue()
            }
        }
    }

    private fun showPlaylistDialogue() {
        val playlistDialogue = PlaylistDialogue(activity, ::onDialogueSubmit)
        playlistDialogue.show()
    }

    private fun onDialogueSubmit(name: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            createPlaylist(AppUser.user_id, name, type).collect {
                withContext(Dispatchers.Main) {
                    loadAllPlaylists()
                }
            }
        }
    }

    private fun setRecyclerList(itemList: List<MediaItemOfListModel>) {
        recAdapter.setItem_List(itemList)
        ChoosePlRecycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = recAdapter
        }
    }

    private fun loadAllPlaylists() {
        UiManager().setProgressBarState(choose_pl_fav_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            loadPlaylists(AppUser.user_id, type).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(choose_pl_fav_progress, false)
                    setRecyclerList(it)
                }
            }
        }
    }
}