package com.example.generalattendance.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {
    private val callPermissionList =
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE
        )
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var storedOnResult: (Boolean) -> Unit = {}

    fun checkCallPermission(context: Context): Boolean {
        return callPermissionList
            .map{ ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED}
            .all{it}
    }

    fun checkWriteSettingPermission(context: Context): Boolean {
        return Settings.System.canWrite(context)
    }

    fun requestWriteSettingPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.setData(Uri.parse("package:" + context.packageName))
        context.startActivity(intent)
    }

    fun requestCallPermission(onResult: (Boolean) -> Unit){
        permissionLauncher?.let { launcher ->
            storedOnResult = onResult // Store the callback
            launcher.launch(callPermissionList)
        }
    }

    fun storeResult(result: Boolean){
        storedOnResult(result)
    }

    fun initializeLauncher(launcher: ActivityResultLauncher<Array<String>>){
        permissionLauncher = launcher
    }
}