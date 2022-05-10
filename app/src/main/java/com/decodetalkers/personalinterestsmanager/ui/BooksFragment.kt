package com.decodetalkers.personalinterestsmanager.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.ui.adapters.SectionRecycler
import com.decodetalkers.personalinterestsmanager.ui.customview.MediaHeader
import com.decodetalkers.personalinterestsmanager.ui.util.UiManager
import com.decodetalkers.personalinterestsmanager.viewmodels.HomeScreensViewModel
import kotlinx.android.synthetic.main.fragment_books.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class BooksFragment : Fragment() {

    private var sectionRecyclerAdapter = SectionRecycler(::loadBookDetailsForActivity)
    private lateinit var homeScreensVM: HomeScreensViewModel
    private lateinit var cJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleCardBooks.setHeaderType(MediaHeader.HEADER_BOOKS)

        homeScreensVM =
            ViewModelProvider(requireActivity()).get(HomeScreensViewModel::class.java)

        setRecyclerList(arrayListOf())

        loadSections(false)

        swipeRefreshBooks.setOnRefreshListener {
            setRecyclerList(arrayListOf())
            loadSections(true)
            swipeRefreshBooks.isRefreshing = false
        }
    }

    private fun loadSections(reload: Boolean) {
        try {
            try {
                UiManager().setProgressBarState(books_progress, true)
            } catch (e: Exception) {
            }
            cJob = CoroutineScope(Dispatchers.IO).launch {
                homeScreensVM.getBooksHomePage(AppUser.user_id, reload).collect {
                    withContext(Dispatchers.Main) {
                        try {
                            try {
                                UiManager().setProgressBarState(books_progress, false)
                            } catch (e: Exception) {
                            }
                            setRecyclerList(it)
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cJob.cancel()
    }

    private fun setRecyclerList(list: List<SectionModel>) {
        sectionRecyclerAdapter.setItem_List(list)
        books_section_recycler.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = sectionRecyclerAdapter
        }
    }


    private fun loadBookDetailsForActivity(
        bookId: String,
        bookImageView: ImageView,
        type: String = ""
    ) {
        UiManager().setProgressBarState(books_progress, true)
        CoroutineScope(Dispatchers.IO).launch {
            homeScreensVM.getBookById(bookId, AppUser.user_id).collect {
                withContext(Dispatchers.Main) {
                    UiManager().setProgressBarState(books_progress, false)
                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
                    intent.putExtra("book_model", it)
                    val actOptions = ActivityOptions.makeSceneTransitionAnimation(
                        requireActivity(),
                        bookImageView,
                        "SharedPoster"
                    )
                    startActivity(intent, actOptions.toBundle())
                }
            }
        }
    }
}