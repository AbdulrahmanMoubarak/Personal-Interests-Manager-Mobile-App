package com.decodetalkers.personalinterestsmanager.retrofit

import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.models.MovieModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.models.SongModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitPimApi {

    @GET("/movies/search")
    suspend fun getMoviesSearchResults(
        @Query("name") name :String
    ) : Response<List<MediaItemOfListModel>>

    @GET("/movies/search")
    suspend fun getMovieById(
        @Query("id") id :String
    ) : Response<MovieModel>

    @GET("/movies/main")
    suspend fun getMoviesHomeResults(
        @Query("userId") userId: Int
    ) : Response<List<SectionModel>>

    @GET("/movies/credits")
    suspend fun getMovieCastById(
        @Query("movieId") movieId: Int
    ): Response<List<MediaItemOfListModel>>

    @POST("/movies/rate")
    suspend fun addMovieRating(
        @FieldMap params: HashMap<String, String>
    ): Response<ResponseBody>

    @GET("/music/main")
    suspend fun getMusicHomeResults(
        @Query("userId") userId: Int
    ): Response<List<SectionModel>>

    @GET("/music/search")
    suspend fun getSongById(
        @Query("id") id :String
    ): Response<SongModel>

    @GET("/music/search")
    suspend fun searchForSong(
        @Query("query") query: String
    ): Response<List<MediaItemOfListModel>>


}