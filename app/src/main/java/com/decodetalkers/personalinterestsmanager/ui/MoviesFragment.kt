package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionAdapter
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.fragment_movies.*
import com.decodetalkers.personalinterestsmanager.application.AppUser
import kotlinx.coroutines.*

class MoviesFragment : Fragment() {

    private var sectionRecyclerAdapter = SectionAdapter(::loadMovieDetailsForActivity)
    private lateinit var homeScreensVM: HomeScreensViewModel
    private lateinit var cJob: Job

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

        homeScreensVM =
            ViewModelProvider(requireActivity()).get(HomeScreensViewModel::class.java)

        setRecyclerList(arrayListOf())

        swipeRefreshMovies.setOnRefreshListener {
            setRecyclerList(arrayListOf())
            loadSectionsAsync(true)
            swipeRefreshMovies.isRefreshing = false
        }

        titleCardMovies.setUserImage(AppUser.user_image)

        loadSectionsAsync(false)
    }

    private fun loadSectionsAsync(reload: Boolean) {
        UiManager().setProgressBarState(MoviesloadingProgressBar, true)
        if (HomeScreensViewModel.isMoviesLoaded && !reload) {
            loadSections(reload)
        } else {
            try {
                cJob = CoroutineScope(Dispatchers.IO).launch {
                    homeScreensVM.getMoviesHomePageResultsAsync(AppUser.user_id, reload).collect {
                        withContext(Dispatchers.Main) {
                            try {
                                if(it.section_mediaItems.size > 0) {
                                    sectionRecyclerAdapter.addSectionItem(it)
                                }
                                Log.d("MoviesFragment", "loadSectionsAsync: ${it.section_name} loaded")
                                UiManager().setProgressBarState(MoviesloadingProgressBar, false)
                                UiManager().setProgressBarState(movies_progress, false)
                            }catch (e: Exception){

                            }
                        }
                    }
                }
            }catch (e:Exception){

            }
        }
    }

    private fun loadSections(reload: Boolean) {
        try {
            UiManager().setProgressBarState(movies_progress, true)
            cJob = CoroutineScope(Dispatchers.IO).launch {
                homeScreensVM.getMoviesHomePage(AppUser.user_id, reload).collect {
                    withContext(Dispatchers.Main) {
                        try {
                            UiManager().setProgressBarState(movies_progress, false)
                            UiManager().setProgressBarState(MoviesloadingProgressBar, false)
                            setRecyclerList(it)
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun setRecyclerList(list: List<SectionModel>) {
        sectionRecyclerAdapter.setItem_List(list)
        movies_section_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = sectionRecyclerAdapter
        }
    }

    private fun loadMovieDetailsForActivity(
        movieId: String,
        movieImageView: ImageView,
        type: String = ""
    ) {
        UiManager().setProgressBarState(movies_progress, true)
        cJob = CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getMovieById(movieId).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(movies_progress, false)
                    val intent = Intent(requireContext(), MovieDetailActivity::class.java)
                    intent.putExtra("movie_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                        requireActivity(),
                        movieImageView,
                        "SharedPoster"
                    )
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
    }


}//