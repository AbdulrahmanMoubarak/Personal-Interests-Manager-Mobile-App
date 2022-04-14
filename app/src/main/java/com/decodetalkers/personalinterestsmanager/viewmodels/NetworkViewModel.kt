package com.decodetalkers.personalinterestsmanager.viewmodels

import androidx.lifecycle.ViewModel
import com.decodetalkers.personalinterestsmanager.application.AppUser
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
        try {
            val response = RetrofitBuilder.pimApiService.getMoviesHomeResults(userId)
                .body() as List<SectionModel>
            emit(response)
        }catch (e:Exception){
            emit(arrayListOf())
        }
    }

    fun getMovieById(movieId: String) = flow {
        val response = RetrofitBuilder.pimApiService.getMovieById(movieId, AppUser.user_id.toString()).body() as MovieModel
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
            val response = RetrofitBuilder.pimApiService.addMovieRating(userId,movieId, rating)
            emit(true)
        } catch (e: Exception){
            emit(false)
        }
    }

    fun getBooksHomePage(userId: Int) = flow {
        val response = RetrofitBuilder.pimApiService.getBooksHomeResults(userId)
            .body() as List<SectionModel>
        emit(response)
    }

}