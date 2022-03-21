package com.decodetalkers.personalinterestsmanager.models

import com.google.gson.annotations.SerializedName


class SectionModel(
    @SerializedName("section_name")
    var section_name: String,
    @SerializedName("items")
    var section_mediaItems: List<MediaItemOfListModel>
) {
}