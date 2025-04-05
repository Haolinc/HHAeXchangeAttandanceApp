package com.example.generalattendance.permission

import android.content.Context

class CallPermissionChecker: PermissionChecker {
    override fun hasPermission(context: Context): Boolean {
        return PermissionHelper.checkCallPermission(context)
    }
}