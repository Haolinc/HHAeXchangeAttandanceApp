package com.example.generalattendance.permission

import android.content.Context

class WriteSettingPermissionChecker: PermissionChecker {
    override fun hasPermission(context: Context): Boolean {
        return PermissionHelper.checkWriteSettingPermission(context)
    }
}