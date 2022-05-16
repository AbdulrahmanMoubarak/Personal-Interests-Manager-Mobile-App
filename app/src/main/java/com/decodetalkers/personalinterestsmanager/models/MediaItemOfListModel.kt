package com.decodetalkers.personalinterestsmanager.models

import com.google.gson.annotations.SerializedName

data class MediaItemOfListModel(
    @SerializedName("item_id")
    var item_id: String,
    @SerializedName("item_name")
    var item_name: String,
    @SerializedName("image")
    var item_image: String,
    @SerializedName("type")
    var item_type: String
){
    var selected = false
}