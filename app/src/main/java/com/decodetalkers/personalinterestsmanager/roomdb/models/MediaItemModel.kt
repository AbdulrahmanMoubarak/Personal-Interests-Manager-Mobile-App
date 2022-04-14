package com.decodetalkers.personalinterestsmanager.roomdb.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "items_table")
data class MediaItemModel(
    val section_id: Int,
    @PrimaryKey(autoGenerate = false) val item_id: String,
    val item_name: String,
    val item_image: String,
    val item_type: String
): Serializable