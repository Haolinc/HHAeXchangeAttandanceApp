package com.example.generalattendance.permission

import android.content.Context

interface PermissionChecker {
    fun hasPermission(context: Context): Boolean
}