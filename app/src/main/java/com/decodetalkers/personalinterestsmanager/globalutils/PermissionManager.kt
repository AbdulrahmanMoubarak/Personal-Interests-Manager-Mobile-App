package com.decodetalkers.personalinterestsmanager.globalutils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionManager {

    fun checkForPermission(context: Context, permission: String): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }
            else -> {
                return false
            }
        }
    }
}