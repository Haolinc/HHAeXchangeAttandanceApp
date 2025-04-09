package com.example.generalattendance.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxSize()
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        Image(
            painter =  painterResource(R.drawable.write_setting_guide_image),
            contentDescription = "Write Setting Guide Image",
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.fragment_permission_rational_text),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                PermissionHelper.requestWriteSettingPermission(context)
            },
            modifier = Modifier.fillMaxWidth(),
        ){
            Text(text = stringResource(R.string.fragment_permission_request_permission_button_text), fontSize = 20.sp)
        }
    }
}
