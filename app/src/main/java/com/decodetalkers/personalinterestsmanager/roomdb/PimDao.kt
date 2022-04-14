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
    suspend fun insertSection(section: SectionModel): Long

    @Insert
    suspend fun insertMediaItem(mediaItem: MediaItemModel)

    @Query("SELECT * FROM $ITEMS_TABLE WHERE section_id = (:sectionId)")
    suspend fun getSectionItems(sectionId: Int):List<MediaItemModel>

    @Query("SELECT * FROM $SECTIONS_TABLE WHERE media_type = (:mediaType)")
    suspend fun getTypeSections(mediaType: String):List<SectionModel>

    @Query("DELETE FROM $SECTIONS_TABLE")
    suspend fun deleteAllSections()

    @Query("DELETE FROM $ITEMS_TABLE")
    suspend fun deleteAllItems()
}