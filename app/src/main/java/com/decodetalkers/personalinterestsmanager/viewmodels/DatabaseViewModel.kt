package com.decodetalkers.personalinterestsmanager.viewmodels

import androidx.lifecycle.ViewModel
import com.decodetalkers.personalinterestsmanager.models.SectionModel
import com.decodetalkers.personalinterestsmanager.roomdb.PimBackupDatabase
import com.decodetalkers.radioalarm.application.MainApplication
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class DatabaseViewModel: ViewModel(){
    companion object{
        const val MOVIES = "movies"
        const val BOOKS = "books"
        const val MUSIC = "music"
    }

    private val databaseInstance = PimBackupDatabase.getInstance(MainApplication.getAppContext())

    fun getSectionsForMediaType(mediaType: String)= flow{
        emit(databaseInstance?.getSpecificTypeSections(mediaType))
    }

    fun insertSections(sections: List<SectionModel>,mediaType: String) = flow{
        try {
            databaseInstance?.insertTypeSections(sections, mediaType)
            emit(true)
        }catch (e: Exception){
            emit(false)
        }
    }

    fun deleteDatabaseRecords()=flow{
        try {
            databaseInstance?.deleteAllData()
            emit(true)
        }catch (e:Exception){
            emit(false)
        }
    }
}