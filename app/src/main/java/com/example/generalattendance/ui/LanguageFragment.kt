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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generalattendance.AppDataStorage
import com.example.generalattendance.viewmodels.UIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val LocalViewModel = compositionLocalOf<UIViewModel> {
    error("UIViewModel not provided")
}

@Composable
fun LanguageFragment(onNavigate: () -> Unit, viewModel: UIViewModel){
    CompositionLocalProvider(LocalViewModel provides viewModel) {
        val appDataStorage = AppDataStorage(LocalContext.current)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ){
            LanguageSelectionButton("English", "en", onNavigate, appDataStorage)
            LanguageSelectionButton("中文", "zh", onNavigate, appDataStorage)
        }
    }
}

@Composable
fun LanguageSelectionButton(text: String, toChangeLanguage: String, onNavigate: () -> Unit, appDataStorage: AppDataStorage){
    val currentViewModel = LocalViewModel.current
    val currentLanguage by currentViewModel.getLanguage().observeAsState(appDataStorage.getLanguage)
    val isSameLanguage = currentLanguage == toChangeLanguage
    Button(
        colors = if (isSameLanguage) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(Color.LightGray),
        onClick =
        {
            if (!isSameLanguage) {
                currentViewModel.setLanguage(toChangeLanguage)
                CoroutineScope(Dispatchers.IO).launch {
                    appDataStorage.setLanguage(toChangeLanguage)
                }
            }
            onNavigate()
        },
        modifier = Modifier.width(250.dp).height(70.dp).padding(15.dp),
    ) {
        Text(text = text, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}
