package com.decodetalkers.personalinterestsmanager.models

import java.io.Serializable

class ArtistModel(
    var artist_name: String,
    var artist_spotify_id: String,
    var image: String,
    var followers: Int,
    var spotify_profile: String
): Serializable {
}