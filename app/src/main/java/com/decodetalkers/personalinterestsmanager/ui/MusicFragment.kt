package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.MediaItemRecycler.Companion.TYPE_ARTIST
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.NetworkViewModel
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MusicFragment : Fragment() {

    //private var sectionRecyclerAdapter = SectionRecycler(::loadSongDetailsForActivity)
    private var sectionRecyclerAdapter = SectionRecycler(::loadSongDetailsForActivity)
    private lateinit var networkVM: NetworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleCardMusic.setHeaderType(MediaHeader.HEADER_MUSIC)
        networkVM =
            ViewModelProvider(requireActivity()).get(NetworkViewModel::class.java)

        setRecyclerList(arrayListOf())

        loadSections()

        swipeRefreshMusic.setOnRefreshListener {
            setRecyclerList(arrayListOf())
            loadSections()
            swipeRefreshMusic.isRefreshing = false
        }
    }

    private fun loadSections() {
        UiManager().setProgressBarState(music_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            networkVM.getMusicHomePage(2018170873).collect {
                withContext(Dispatchers.Main) {
                    try {
                        UiManager().setProgressBarState(music_progress, false)
                        setRecyclerList(it)
                    }catch (e:Exception){

                    }
                }
            }
        }
    }

    private fun setRecyclerList(list: List<SectionModel>) {
        sectionRecyclerAdapter.setItem_List(list)
        music_section_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = sectionRecyclerAdapter
        }
    }

    private fun loadSongDetailsForActivity(songId: String, songImageView: ImageView, type:String = "") {
        if(type != TYPE_ARTIST) {
            UiManager().setProgressBarState(music_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                networkVM.getSongById(songId).collect {
                    withContext(Dispatchers.Main) {
                        UiManager().setProgressBarState(music_progress, false)
                        val intent = Intent(requireContext(), SongDetailActivity::class.java)
                        intent.putExtra("song_model", it)
                        val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                            requireActivity(),
                            songImageView,
                            "SharedPoster"
                        )
                        startActivity(intent, actOptions.toBundle())
                    }
                }
            }
        }
    }

}