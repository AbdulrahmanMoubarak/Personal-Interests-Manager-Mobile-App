package com.decodetalkers.personalinterestsmanager.retrofit

import com.decodetalkers.personalinterestsmanager.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface RetrofitPimApi {

    @GET("/movies/search")
    suspend fun getMoviesSearchResults(
        @Query("name") name :String
    ) : Response<List<MediaItemOfListModel>>

    @GET("/movies/search")
    suspend fun getMovieById(
        @Query("id") id :Int,
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
        @Query("movieId") movieId: Int,
        @Query("userId") userId: Int
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

    @GET("/books/main")
    suspend fun getBooksHomeResults(
        @Query("userId") userId: Int
    ) : Response<List<SectionModel>>

    @GET("/books/search")
    suspend fun getBooksSearchResults(
        @Query("q") query: String
    ):Response<List<MediaItemOfListModel>>

    @GET("/books/search")
    suspend fun getBookById(
        @Query("id") bookId: String,
        @Query("userId") userId: Int
    ): Response<BookModel>

    @FormUrlEncoded
    @POST("/books/rate")
    suspend fun addBookRating(
        @Field("userId") userId: Int,
        @Field("bookId") bookId: String,
        @Field("bookName") bookName: String,
        @Field("rating") rating: Float,
    ): Response<ResponseBody>

    @GET("/books/book-based-recommendation")
    suspend fun getBookBasedRecommendation(
        @Query("bookId")bookId: String
    ): Response<List<SectionModel>>

    @GET("/movies/main/sections")
    suspend fun getMoviesSectionNames(
        @Query("userId") userId: Int
    ): Response<List<MutableMap<String, String>>>

    @GET("/movies/main/sections/content")
    suspend fun getSectionContent(
        @Query("name") sectionName: String,
        @Query("userId") userId: Int? = null,
        @Query("year") year: Int? = null,
        @Query("genre") genre: Int? = null,
    ): Response<List<MediaItemOfListModel>>
}