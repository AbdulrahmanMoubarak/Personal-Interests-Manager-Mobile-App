package com.decodetalkers.personalinterestsmanager.roomdb.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sections_table")
data class SectionModel(
    val section_name: String,
    @PrimaryKey(autoGenerate = true) val section_id: Int,
    val media_type: String
):Serializable