package com.decodetalkers.personalinterestsmanager.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


class SectionModel(
    @SerializedName("section_name")
    var section_name: String,
    @SerializedName("items")
    var section_mediaItems: List<MediaItemOfListModel>
) {
    var order: Int = 0
}