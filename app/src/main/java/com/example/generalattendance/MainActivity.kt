package com.example.generalattendance

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.collection.forEach
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.generalattendance.enums.RouteEnum
import com.example.generalattendance.ui.ClockingFragment
import com.example.generalattendance.ui.EmployeeInfoFragment
import com.example.generalattendance.ui.EmployeeInfoViewModel
import com.example.generalattendance.ui.LanguageFragment
import com.example.generalattendance.ui.NavigationData
import com.example.generalattendance.ui.SettingFragment
import com.example.generalattendance.ui.UIViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val bottomNavigationList =
        listOf(
            NavigationData(RouteEnum.CLOCKING.name, R.string.ClockingFragment),
            NavigationData(RouteEnum.EMPLOYEE_INFO.name, R.string.EmployeeInfoFragment),
            NavigationData(RouteEnum.SETTING.name, R.string.SettingFragment),
        )
    private val settingNavigationList =
        listOf(
            NavigationData(RouteEnum.LANGUAGE.name, R.string.LanguageFragment),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }

    private fun checkCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

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
        val employeeInfoViewModel: EmployeeInfoViewModel = viewModel()
        val uiViewModel: UIViewModel = viewModel()
        val appDataStorage = AppDataStorage(this)
        val appLocalization by uiViewModel.getLanguage().observeAsState(appDataStorage.getLanguage)
        setAppLocale(appLocalization)

        Scaffold (bottomBar = {
            BottomNavigation(navController)
        }) { paddingValue ->
            NavHost(
                navController = navController,
                startDestination =
                    if (appDataStorage.getIsFirstTime)
                        "${RouteEnum.LANGUAGE.name}/${RouteEnum.CLOCKING.name}"
                    else
                        RouteEnum.CLOCKING.name,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValue),
            ) {
                composable(RouteEnum.CLOCKING.name) {
                    ClockingFragment(employeeInfoViewModel, isCallPermissionGranted)
                }
                composable(RouteEnum.EMPLOYEE_INFO.name) {
                    EmployeeInfoFragment(employeeInfoViewModel)
                }
                composable(RouteEnum.SETTING.name) {
                    SettingFragment(navController, settingNavigationList)
                }
                composable("${RouteEnum.LANGUAGE.name}/{destination}") {backStackEntry ->
                    LanguageFragment(
                        {
                            val nextDestination = backStackEntry.arguments?.getString("destination") ?: RouteEnum.SETTING.name
                            navController.navigate(nextDestination){
                                // Have to pop the current Language stack before going to next destination
                                navController.popBackStack()

                                popUpTo(nextDestination){
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        uiViewModel
                    )
                }
            }
        }
    }

    @Composable
    fun BottomNavigation(navController: NavController) {
        val selectedIcons = listOf(Icons.Filled.Call, Icons.Filled.AccountCircle, Icons.Filled.Settings)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val routeList = bottomNavigationList.map{navData -> navData.route}
        AnimatedVisibility(
            visible = routeList.contains(currentDestination?.route),
            enter = EnterTransition.None,
            exit = ExitTransition.None,
            content = {
                NavigationBar {
                    bottomNavigationList.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    selectedIcons[index],
                                    contentDescription = item.route
                                )
                            },
                            label =
                            {
                                Text(stringResource(item.stringResourceId))
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                if (currentDestination!!.route != item.route) {
                                    navController.popBackStack()
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
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

