package com.decodetalkers.personalinterestsmanager.models

import java.io.Serializable

class BookModel(
    var isbn: String,
    var book_title: String,
    var book_author: String,
    var year_of_publication: String,
    var publisher: String,
    var image_url: String,
    var subtitle: String,
    var description: String,
    var categories: String,
    var preview_link: String,
    var user_rating: Float
): Serializable