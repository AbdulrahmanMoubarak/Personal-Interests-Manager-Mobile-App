package com.decodetalkers.personalinterestsmanager.roomdb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Room
import androidx.room.RoomDatabase
import com.decodetalkers.personalinterestsmanager.models.MediaItemOfListModel
import com.decodetalkers.personalinterestsmanager.roomdb.models.MediaItemModel
import com.decodetalkers.personalinterestsmanager.roomdb.models.SectionModel

@Database(entities = [SectionModel::class, MediaItemModel::class], version = 1)
abstract class PimBackupDatabase: RoomDatabase() {
    abstract fun dao(): PimDao

    companion object {
        const val DATABASE_NAME = "pim_sub_database"
        private var instance: PimBackupDatabase? = null
        @Synchronized
        fun getInstance(context: Context): PimBackupDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    PimBackupDatabase::class.java, DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }

    suspend fun getSpecificTypeSections(type: String):List<com.decodetalkers.personalinterestsmanager.models.SectionModel>{
        val secList = arrayListOf<com.decodetalkers.personalinterestsmanager.models.SectionModel>()
        val sectionsFromDb = dao().getTypeSections(type)
        for (section in sectionsFromDb){
            val secItemsFromDb = dao().getSectionItems(section.section_id)
            val itemList = arrayListOf<MediaItemOfListModel>()
            for(item in secItemsFromDb){
                itemList.add(MediaItemOfListModel(item.item_id, item.item_name, item.item_image, item.item_type))
            }
            secList.add(com.decodetalkers.personalinterestsmanager.models.SectionModel(section.section_name, itemList))
        }
        Log.d(PimBackupDatabase::class.java.simpleName, "getSpecificTypeSections: ${type} size = ${secList.size}")
        return secList
    }

    suspend fun insertTypeSections(sections: List<com.decodetalkers.personalinterestsmanager.models.SectionModel>, type: String){
        for (section in sections){
            dao().insertSection(SectionModel(section.section_name, 0, type))
            val sectionId = dao().getSectionId(section.section_name, type)
            Log.d(PimBackupDatabase::class.java.simpleName, "Db sectionId: ${type} = ${sectionId}")
            for(item in section.section_mediaItems){
                dao().insertMediaItem(MediaItemModel(sectionId,item.item_id, item.item_name, item.item_image, item.item_type))
            }
        }
    }

    suspend fun deleteAllData(type: String){
        val secList = dao().getSectionsIdsByType(type)
        for (id in secList){
            dao().deleteAllItemsOfSection(id)
        }
        dao().deleteAllSections(type)
    }
}