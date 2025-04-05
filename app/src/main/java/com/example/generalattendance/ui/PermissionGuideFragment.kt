package com.example.generalattendance.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.generalattendance.R
import com.example.generalattendance.permission.PermissionChecker
import com.example.generalattendance.permission.PermissionHelper
import com.example.generalattendance.permission.WriteSettingPermissionChecker

@Composable
fun PermissionGuideFragment(onNavigate: () -> Unit){
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val context = LocalContext.current
    val toastString = stringResource(R.string.toast_permission_granted)
    val writeSettingPermissionChecker: PermissionChecker = remember{ WriteSettingPermissionChecker() }

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            if (writeSettingPermissionChecker.hasPermission(context)) {
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
                onNavigate()
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        Text("Need Permission")
        RequestPermissionButton(context)
    }
}

@Composable
fun RequestPermissionButton(context: Context){
    Button(
        {
            PermissionHelper.requestWriteSettingPermission(context)
        }
    ){
        Text("Request Permission")
    }
}
