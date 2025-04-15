package com.example.generalattendance

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.generalattendance.enums.RouteEnum
import com.example.generalattendance.permission.PermissionHelper
import com.example.generalattendance.ui.ClockingFragment
import com.example.generalattendance.ui.EmployeeInfoFragment
import com.example.generalattendance.viewmodels.EmployeeInfoViewModel
import com.example.generalattendance.ui.LanguageFragment
import com.example.generalattendance.ui.NavigationData
import com.example.generalattendance.ui.PermissionGuideFragment
import com.example.generalattendance.ui.SettingFragment
import com.example.generalattendance.viewmodels.UIViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
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
            NavigationData(RouteEnum.PERMISSION.name, R.string.PermissionFragment)
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            PermissionHelper.storeResult(!isGranted.containsValue(false)) // Delegate to Helper
        }
        PermissionHelper.initializeLauncher(permissionLauncher)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }


    @Composable
    fun AppNavigation() {
        // Main Navigation UI
        val navController = rememberNavController()
        val employeeInfoViewModel: EmployeeInfoViewModel = viewModel()
        val uiViewModel: UIViewModel = viewModel()
        val appDataStorage = remember{ AppDataStorage(this) }
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
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable(RouteEnum.CLOCKING.name) {
                    ClockingFragment(employeeInfoViewModel, uiViewModel)
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
                composable("${RouteEnum.PERMISSION.name}/{destination}") {backStackEntry ->
                    PermissionGuideFragment {
                        val nextDestination = backStackEntry.arguments?.getString("destination")
                            ?: RouteEnum.SETTING.name
                        navController.navigate(nextDestination) {
                            navController.popBackStack()
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNavigation(navController: NavController) {
        val selectedIcons = listOf(Icons.Filled.Call, Icons.Filled.AccountCircle, Icons.Filled.Settings)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentNavigationTop = navBackStackEntry?.destination
        val currentRoute = currentNavigationTop?.route
        val routeList = bottomNavigationList.map{navData -> navData.route}
        AnimatedVisibility(
            visible = routeList.contains(currentRoute),
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
                            selected = currentNavigationTop?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(currentRoute!!){
                                            inclusive = true
                                        }
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

