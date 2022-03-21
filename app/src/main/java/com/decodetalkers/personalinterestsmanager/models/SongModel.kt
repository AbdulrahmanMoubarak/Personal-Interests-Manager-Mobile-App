package com.decodetalkers.personalinterestsmanager.models

import java.io.Serializable

class SongModel(
    var song_id: Int,
    var album_spotify_id: String,
    var artists_spotify_id: String,
    var title: String,
    var song_spotify_id: String,
    var spotify_link: String,
    var youtube_id: String,
    var image: String,
    var artists: List<ArtistModel>
): Serializable{
}