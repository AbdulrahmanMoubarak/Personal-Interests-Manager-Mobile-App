package com.decodetalkers.personalinterestsmanager.retrofit

import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.models.MovieModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.models.SongModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitPimApi {

    @GET("/movies/search")
    suspend fun getMoviesSearchResults(
        @Query("name") name :String
    ) : Response<List<MediaItemOfListModel>>

    @GET("/movies/search")
    suspend fun getMovieById(
        @Query("id") id :String,
        @Query("userId") userId :String
    ) : Response<MovieModel>

    @GET("/movies/main")
    suspend fun getMoviesHomeResults(
        @Query("userId") userId: Int
    ) : Response<List<SectionModel>>

    @GET("/movies/credits")
    suspend fun getMovieCastById(
        @Query("movieId") movieId: Int
    ): Response<List<MediaItemOfListModel>>

    @FormUrlEncoded
    @POST("/movies/rate")
    suspend fun addMovieRating(
        @Field("userId") userId: Int,
        @Field("movieId") movieId: Int,
        @Field("rating") rating: Float,
    ): Response<ResponseBody>

    @GET("/movies/movie-based-recommendation")
    suspend fun getMovieBasedRecommendation(
        @Query("movieId") movieId: Int
    ): Response<List<SectionModel>>

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

    @GET("/music/song-based-recommendation")
    suspend fun getSongBasedRecommendation(
        @Query("songId") songId: String
    ): Response<List<SectionModel>>

    @FormUrlEncoded
    @POST("/song/listening")
    suspend fun updateSongListeningTimes(
        @Field("userId") userId: Int,
        @Field("songId") songId: String
    ): Response<ResponseBody>



}