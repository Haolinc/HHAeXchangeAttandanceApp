package com.example.generalattendance.ui

import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
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
    Column(
        modifier = Modifier
            .clickable(onClick = onNavigate)
            .fillMaxWidth()
    ){
        Text(text = stringResource(stringResourceId), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp))
        HorizontalDivider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(horizontal = 30.dp))
    }
}
