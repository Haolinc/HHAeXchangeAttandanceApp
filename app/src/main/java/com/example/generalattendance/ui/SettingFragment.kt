package com.example.generalattendance.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun SettingFragment(navController: NavController, settingRouteList: List<NavigationData>){
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(10.dp),
    ){
        items(settingRouteList){ element ->
            SettingBox(element.route, element.stringResourceId, navController)
        }
    }
}

@Composable
fun SettingBox(route: String, stringResourceId: Int, navController: NavController){
    Box(
        modifier = Modifier.clickable(onClick = {navController.navigate(route)})
    ){
        Text(text = stringResource(stringResourceId), fontSize = 20.sp)
    }
}
