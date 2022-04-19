package com.decodetalkers.personalinterestsmanager.roomdb
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.decodetalkers.personalinterestsmanager.roomdb.models.MediaItemModel
import com.decodetalkers.personalinterestsmanager.roomdb.models.SectionModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface PimDao {
    companion object{
        const val ITEMS_TABLE = "items_table"
        const val SECTIONS_TABLE = "sections_table"
    }

    @Insert
    suspend fun insertSection(section: SectionModel)

    @Insert
    suspend fun insertMediaItem(mediaItem: MediaItemModel)

    @Query("SELECT * FROM $ITEMS_TABLE WHERE section_id = (:sectionId)")
    suspend fun getSectionItems(sectionId: Int):List<MediaItemModel>

    @Query("SELECT section_id FROM $SECTIONS_TABLE WHERE section_name = (:name) AND media_type = (:type)")
    suspend fun getSectionId(name: String, type: String): Int

    @Query("SELECT section_id FROM $SECTIONS_TABLE WHERE media_type = (:type)")
    suspend fun getSectionsIdsByType(type: String): List<Int>

    @Query("SELECT * FROM $SECTIONS_TABLE WHERE media_type = (:mediaType)")
    suspend fun getTypeSections(mediaType: String):List<SectionModel>

    @Query("DELETE FROM $SECTIONS_TABLE WHERE media_type = (:type)")
    suspend fun deleteAllSections(type: String)

    @Query("DELETE FROM $ITEMS_TABLE WHERE section_id = (:sectionId)")
    suspend fun deleteAllItemsOfSection(sectionId: Int)
}