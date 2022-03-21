package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.ContentUris
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
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.NetworkViewModel
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.provider.MediaStore.Video.Thumbnails.VIDEO_ID
import android.util.Log

class MoviesFragment : Fragment() {

    private var sectionRecyclerAdapter = SectionRecycler(::loadMovieDetailsForActivity)
    private lateinit var networkVM: NetworkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleCardMovies.setHeaderType(MediaHeader.HEADER_MOVIES)

        networkVM =
            ViewModelProvider(requireActivity()).get(NetworkViewModel::class.java)

        setRecyclerList(arrayListOf())

        loadSections()

        swipeRefreshMovies.setOnRefreshListener {
            setRecyclerList(arrayListOf())
            loadSections()
            swipeRefreshMovies.isRefreshing = false
        }
    }

    private fun loadSections() {
        try {
            UiManager().setProgressBarState(movies_progress, true)
            CoroutineScope(Dispatchers.IO).launch {
                networkVM.getMoviesHomePage(1).collect {
                    withContext(Dispatchers.Main) {
                        try {
                            UiManager().setProgressBarState(movies_progress, false)
                            setRecyclerList(it)
                        }catch (e:Exception){

                        }
                    }
                }
            }
        }catch (e: Exception){

        }
    }

    private fun setRecyclerList(list: List<SectionModel>){
        sectionRecyclerAdapter.setItem_List(list)
        movies_section_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = sectionRecyclerAdapter
        }
    }

    private fun loadMovieDetailsForActivity(movieId: String, movieImageView: ImageView, type: String = ""){
        UiManager().setProgressBarState(movies_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            networkVM.getMovieById(movieId).collect{
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(movies_progress, false)
                    val intent = Intent(requireContext(), MovieDetailActivity::class.java)
                    intent.putExtra("movie_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), movieImageView, "SharedPoster")
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
    }


}//