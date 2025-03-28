package com.example.generalattendance.ui

import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.generalattendance.R
import com.example.generalattendance.enums.RouteEnum


@Composable
fun SettingFragment(navController: NavController, settingRouteList: List<NavigationData>){
    val context = LocalContext.current
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ){
        items(settingRouteList){ element ->
            when (element.route) {
                RouteEnum.LANGUAGE.name -> {
                    SettingBox(
                        { navController.navigate("${element.route}/${RouteEnum.SETTING}") },
                        element.stringResourceId
                    )
                }
                RouteEnum.PERMISSION.name -> {
                    val toastString = stringResource(R.string.toast_permission_already_granted)
                    SettingBox(
                        {
                            if (Settings.System.canWrite(context))
                                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
                            else
                                navController.navigate("${element.route}/${RouteEnum.SETTING}")
                        },
                        element.stringResourceId
                    )
                }
            }
        }
    }
}

@Composable
fun SettingBox(onNavigate: () -> Unit, stringResourceId: Int){
    Box(
        modifier = Modifier.clickable(onClick = onNavigate)
    ){
        Text(text = stringResource(stringResourceId), fontSize = 20.sp)
    }
}
