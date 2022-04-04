package com.decodetalkers.personalinterestsmanager.viewmodels

import androidx.lifecycle.ViewModel
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.models.MovieModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.models.SongModel
import com.decodetalkers.personalinterestsmanager.retrofit.RetrofitBuilder
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class NetworkViewModel: ViewModel() {

    fun getMoviesSearchResults(name: String) = flow {
        val response = RetrofitBuilder.pimApiService.getMoviesSearchResults(name)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun getMusicSearchResults(query: String) = flow {
        val response = RetrofitBuilder.pimApiService.searchForSong(query)
            .body() as List<MediaItemOfListModel>
        emit(response)
    }

    fun getMoviesHomePage(userId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMoviesHomeResults(userId)
            .body() as List<SectionModel>
        emit(response)
    }

    fun getMovieById(movieId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieById(movieId).body() as MovieModel
        emit(response)
    }

    fun getMusicHomePage(userId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getMusicHomeResults(userId)
            .body() as List<SectionModel>
        emit(response)
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

    fun addMovieRating(userId: Int, movieId: Int, rating: Float) = flow{
        try {
            val params = HashMap<String, String>()
            params["userId"] = userId.toString()
            params["movieId"] = movieId.toString()
            params["rating"] = rating.toString()
            val response = RetrofitBuilder.pimApiService.addMovieRating(params)
            emit(true)
        } catch (e: Exception){
            emit(false)
        }
    }
}