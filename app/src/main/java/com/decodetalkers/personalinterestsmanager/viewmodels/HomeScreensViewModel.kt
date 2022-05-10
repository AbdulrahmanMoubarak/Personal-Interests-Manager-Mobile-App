package com.decodetalkers.personalinterestsmanager.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.models.*
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.roomdb.PimBackupDatabase
import com.decodetalkers.radioalarm.application.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeScreensViewModel : ViewModel() {

    companion object {
        const val MOVIES = "movies"
        const val BOOKS = "books"
        const val MUSIC = "music"

        var isMoviesLoaded = false
        var isMusicLoaded = false
        var isBooksLoaded = false

        private var moviesSections: MutableList<SectionModel> = mutableListOf()
        private lateinit var musicSections: List<SectionModel>
        private lateinit var booksSections: List<SectionModel>
    }

    private val databaseInstance = PimBackupDatabase.getInstance(MainApplication.getAppContext())

    fun getMoviesSearchResults(name: String) = flow {
        val response = RetrofitBuilder.pimApiService.getMoviesSearchResults(name)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun getBooksSearchResults(query: String) = flow {
        val response = RetrofitBuilder.pimApiService.getBooksSearchResults(query)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun getMusicSearchResults(query: String) = flow {
        val response = RetrofitBuilder.pimApiService.searchForSong(query)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun getMoviesHomePage(userId: Int, reload: Boolean) = flow {
        try {
            if (!isMoviesLoaded || reload) {
                val response = RetrofitBuilder.pimApiService.getMoviesHomeResults(userId)
                    .body() as MutableList<SectionModel>
                moviesSections = response
                isMoviesLoaded = true
                emit(response)
            } else {
                emit(moviesSections)
            }
        } catch (e: Exception) {
            try {
                Toast.makeText(
                    MainApplication.getAppContext(),
                    "Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {

            }
        }
    }

    fun getMoviesHomePageResultsAsync(userId: Int, reload: Boolean) = flow {
        isMoviesLoaded=false
        val sectionLines = RetrofitBuilder.pimApiService.getMoviesSectionNames(userId)
            .body() as List<MutableMap<String, String>>
        var i = 0
        for (item in sectionLines) {
            item["seq"] = i.toString()
            getMovieSectionItems(item).collect {
                emit(SectionModel(item["name"]!!,it).apply {
                    order = item["seq"]!!.toInt()
                })
            }
            i++
        }
    }

    private fun getSectionItems(secList: List<Map<String, String>>) = flow{
        try {
            CoroutineScope(Dispatchers.IO).launch {
                for (section in secList) {
                    getMovieSectionItems(section).collect {
                        emit(it)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    fun getMovieSectionItems(section: Map<String, String>) = flow{
        val name = section["name"]!!.replace(' ', '+')
        val userId = if (section["userId"] != null) section["userId"] else null
        val year = if (section["year"] != null) section["year"] else null
        val genre = if (section["genre"] != null) section["genre"] else null
        val response = RetrofitBuilder.pimApiService.getSectionContent(name, userId?.toInt(), year?.toInt(), genre?.toInt())
            .body() as List<MediaItemOfListModel>
        moviesSections.add(SectionModel(section["name"]!!,response))
        isMoviesLoaded = true
        emit(response)
    }

    fun getMovieById(movieId: String) = flow {
        val response =
            RetrofitBuilder.pimApiService.getMovieById(movieId.toInt(), AppUser.user_id.toString())
                .body() as MovieModel
        emit(response)
    }

    fun getMusicHomePage(userId: Int, reload: Boolean) = flow {
        try {
            if (!isMusicLoaded || reload) {
                val response = RetrofitBuilder.pimApiService.getMusicHomeResults(userId)
                    .body() as List<SectionModel>
                musicSections = response
                isMusicLoaded = true
                emit(response)
            } else {
                emit(musicSections)
            }
        } catch (e: Exception) {
            Toast.makeText(
                MainApplication.getAppContext(),
                "Check Your Internet Connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getSongById(songId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getSongById(songId).body() as SongModel
        emit(response)
    }

    fun getCastByMovieId(movieId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieCastById(movieId)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun addMovieRating(userId: Int, movieId: Int, rating: Float) = flow {
        try {
            val response = RetrofitBuilder.pimApiService.addMovieRating(userId, movieId, rating)
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

    fun getBooksHomePage(userId: Int, reload: Boolean) = flow {
        try {
            if (!isBooksLoaded || reload) {
                val response = RetrofitBuilder.pimApiService.getBooksHomeResults(userId)
                    .body() as List<SectionModel>
                booksSections = response
                isBooksLoaded = true
                emit(response)
            } else {
                emit(booksSections)
            }
        } catch (e: Exception) {
            try {
                Toast.makeText(
                    MainApplication.getAppContext(),
                    "Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }catch (e: Exception){

            }
        }
    }

    fun getBookById(bookId: String, userId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getBookById(bookId, userId)
            .body() as BookModel
        emit(response)
    }

    fun addBookRating(userId: Int, bookId: String, bookName: String, rating: Float) = flow {
        val response = RetrofitBuilder.pimApiService.addBookRating(userId, bookId, bookName, rating)
        emit(response.code())
    }

    fun getBookBasedRecommendation(bookId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getBookBasedRecommendation(bookId)
            .body() as List<SectionModel>
        emit(response)
    }

    fun getSectionsForMediaType(mediaType: String) = flow {
        emit(databaseInstance?.getSpecificTypeSections(mediaType))
    }

    fun insertSections(sections: List<SectionModel>, mediaType: String) = flow {
        databaseInstance?.insertTypeSections(sections, mediaType)
        emit(true)
    }

    fun deleteDatabaseRecords(type: String) = flow {
        try {
            databaseInstance?.deleteAllData(type)
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }

}