package com.decodetalkers.personalinterestsmanager.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.decodetalkers.personalinterestsmanager.application.AppUser
import com.decodetalkers.personalinterestsmanager.globalutils.SharedPreferencesManager
import com.decodetalkers.personalinterestsmanager.models.*
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import com.decodetalkers.personalinterestsmanager.roomdb.PimBackupDatabase
import com.decodetalkers.radioalarm.application.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeScreensViewModel : ViewModel() {

    companion object {
        const val MOVIES = "movies"
        const val BOOKS = "books"
        const val MUSIC = "music"

        private var isMoviesLoaded = false
        private var isMusicLoaded = false
        private var isBooksLoaded = false

        private lateinit var moviesSections: List<SectionModel>
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
            if(!isMoviesLoaded || reload) {
                val response = RetrofitBuilder.pimApiService.getMoviesHomeResults(userId)
                    .body() as List<SectionModel>
                moviesSections = response
                isMoviesLoaded = true
                emit(response)
            }else{
                emit(moviesSections)
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
            Toast.makeText(
                MainApplication.getAppContext(),
                "Check Your Internet Connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getBookById(bookId: String, userId: Int) = flow{
        val response = RetrofitBuilder.pimApiService.getBookById(bookId, userId)
            .body() as BookModel
        emit(response)
    }

    fun addBookRating(userId: Int, bookId: String, bookName: String, rating: Float) = flow{
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