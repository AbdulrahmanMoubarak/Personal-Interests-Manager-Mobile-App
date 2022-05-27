package com.decodetalkers.personalinterestsmanager.locatmusicloader

import com.decodetalkers.myapplication.locatmusicloader.util.Constants.EXTERNAL_STORAGE_ROOT_PATH
import com.decodetalkers.myapplication.locatmusicloader.util.Constants.INTERNAL_STORAGE_ROOT_PATH
import com.decodetalkers.myapplication.locatmusicloader.util.Constants.SOUND_MEDIATYPE_LIST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class LocalMusicLoader {

    private val musicPathList = mutableListOf<File>()

    fun loadAllLocalMusicPaths(uri: String) = flow {
        fileTraverseHelper(uri)
        emit(musicPathList)
    }

    private fun fileTraverseHelper(cur_path: String) {
        val cur_dir = File(cur_path)
        val fileList = cur_dir.list()
        if (fileList != null && fileList.size != 0) {
            for (path in fileList) {
                val file = File(cur_path+"/"+path)
                if (file.isFile) {
                    val pathSplit = path.split('.')
                    if (SOUND_MEDIATYPE_LIST.contains(pathSplit[pathSplit.size - 1])) {
                        musicPathList.add(File(file.absolutePath))
                    }
                } else {
                    if(!path.contains("Android")) {
                        fileTraverseHelper(file.absolutePath)
                    }
                }
            }
        }
    }
}