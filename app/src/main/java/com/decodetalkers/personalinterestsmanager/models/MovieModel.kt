package com.decodetalkers.personalinterestsmanager.models

import java.io.Serializable

class MovieModel(
    var adult: String,
    var belong_to_collection: String,
    var genres: String,
    var movie_id: Int,
    var original_language: String,
    var overview: String,
    var popularity: Float,
    var poster: String,
    var production_company: String,
    var release_date: String,
    var status: String,
    var tag_line: String,
    var title: String,
    var vote_average: Float,
    var vote_count: Int,
    var imdb_id: String,
    var year: Int,
    var trailer: String,
    var background: String,
    var user_rating: Float
) : Serializable {
}