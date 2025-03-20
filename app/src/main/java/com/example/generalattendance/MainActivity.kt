package com.example.generalattendance

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.generalattendance.ui.ClockingFragment
import com.example.generalattendance.ui.EmployeeInfoFragment
import com.example.generalattendance.ui.EmployeeInfoViewModel
import com.example.generalattendance.ui.LanguageFragment
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: EmployeeInfoViewModel
    private val fragmentNameList =
        listOf(
            "ClockingFragment" to R.string.ClockingFragment,
            "EmployeeInfoFragment" to R.string.EmployeeInfoFragment,
            "LanguageFragment" to R.string.LanguageFragment
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }

    override fun onPause(){
        super.onPause()
        // Save the existing data whenever the app onPause
        println("starting saving")
        val employeeInfoStorage = EmployeeInfoStorage(this)
        lifecycleScope.launch {
            val employeeNum = viewModel.getEmployeeNum().value ?: "" // Default value
            val callNum = viewModel.getCallNum().value ?: ""
            val workNumList = viewModel.getWorkNumList().value ?: emptyList()
            val language = viewModel.getLanguage().value ?: ""

            println("current items: $employeeNum, $callNum, $workNumList, $language")
            try {
                employeeInfoStorage.setEmployeeNum(employeeNum)
                employeeInfoStorage.setCallNum(callNum)
                employeeInfoStorage.setWorkNumList(workNumList)
                employeeInfoStorage.setLanguage(language)
                println("saving done")
            } catch (e: Exception){
                println("exception: $e")
            }

        }
    }

    private fun checkCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Preview
    @Composable
    fun AppNavigation() {
        // Permission
        var isCallPermissionGranted by remember { mutableStateOf(checkCallPermission()) }
        val lifecycleOwner = LocalLifecycleOwner.current
        if (!isCallPermissionGranted) {
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        isCallPermissionGranted = checkCallPermission()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        }
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                isCallPermissionGranted = isGranted // Update the State object's value
            }
        )
        LaunchedEffect(key1 = true) {
            if (!isCallPermissionGranted) {
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }

        // Main Navigation UI
        val navController = rememberNavController()
        viewModel = viewModel()
        val appLocalization by viewModel.getLanguage().observeAsState(EmployeeInfoStorage(this).getLanguage)
        setAppLocale(appLocalization)
        Scaffold (bottomBar = { BottomNavigation(navController) }) { paddingValue ->
            NavHost(
                navController = navController,
                startDestination = fragmentNameList[0].first,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValue),
            ) {
                composable(fragmentNameList[0].first) {
                    ClockingFragment(viewModel, isCallPermissionGranted)
                }
                composable(fragmentNameList[1].first) {
                    EmployeeInfoFragment(viewModel)
                }
                composable(fragmentNameList[2].first) {
                    LanguageFragment(viewModel)
                }
            }
        }
    }

    @Composable
    fun BottomNavigation(navController: NavController) {
        val selectedIcons = listOf(Icons.Filled.Call, Icons.Filled.AccountCircle, Icons.Filled.Menu)
        var selectedItem by remember { mutableIntStateOf(0) }
        NavigationBar {
            fragmentNameList.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            selectedIcons[index],
                            contentDescription = item.first
                        )
                    },
                    label = { Text(stringResource(item.second)) },
                    selected = selectedItem == index,
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(item.first)
                        selectedItem = index
                    }
                )
            }
        }
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        val resources: Resources = this.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        this.createConfigurationContext(configuration)
    }

}

