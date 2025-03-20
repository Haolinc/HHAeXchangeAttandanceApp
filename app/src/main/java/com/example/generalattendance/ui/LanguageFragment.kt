package com.example.generalattendance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generalattendance.EmployeeInfoStorage

private lateinit var currentViewModel: EmployeeInfoViewModel

@Composable
fun LanguageFragment(viewModel: EmployeeInfoViewModel){
    currentViewModel = viewModel
    val employeeInfoStorage = EmployeeInfoStorage(LocalContext.current)
    val language by currentViewModel.getLanguage().observeAsState(employeeInfoStorage.getLanguage)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
        LanguageSelectionButton("English", language, "")
        LanguageSelectionButton("中文", language, "zh")
    }
}

@Composable
fun LanguageSelectionButton(text: String, currentLanguage: String, toChangeLanguage: String){
    Button(
        colors = if (currentLanguage == toChangeLanguage) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(Color.LightGray),
        onClick = { currentViewModel.setLanguage(toChangeLanguage) },
        modifier = Modifier.width(250.dp).height(70.dp).padding(15.dp),
    ) {
        Text(text = text, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}
