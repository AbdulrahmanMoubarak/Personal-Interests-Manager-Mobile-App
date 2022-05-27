package com.decodetalkers.personalinterestsmanager.globalutils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
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

    fun requestPermission(context: androidx.activity.ComponentActivity, permission: String): Boolean{
        var granted = false
        val requestPermissionLauncher =
            context.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                granted = isGranted
            }
        requestPermissionLauncher.launch(
            permission
        )
        return granted
    }
}