package com.example.generalattendance

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {
    private val permissionList =
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE
        )
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var storedOnResult: (Boolean) -> Unit = {}

    fun checkCallPermission(context: Context): Boolean {
        return permissionList
            .map{ ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED}
            .all{it}
    }

    fun requestCallPermission(onResult: (Boolean) -> Unit){
        permissionLauncher?.let { launcher ->
            storedOnResult = onResult // Store the callback
            launcher.launch(permissionList)
        }
    }

    fun storeResult(result: Boolean){
        storedOnResult(result)
    }

    fun initializeLauncher(launcher: ActivityResultLauncher<Array<String>>){
        permissionLauncher = launcher
    }
}