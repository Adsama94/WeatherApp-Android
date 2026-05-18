package com.adsama.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.adsama.weatherapp.ui.details.WeatherDetailScreen
import com.adsama.weatherapp.ui.details.WeatherDetailViewModel
import com.adsama.weatherapp.ui.home.WeatherHomeScreen
import com.adsama.weatherapp.ui.home.WeatherHomeViewModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemInDarkTheme) }

            WeatherAppTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.HomeDestination
                ) {
                    composable<AppDestinations.HomeDestination> {
                        val viewModel = hiltViewModel<WeatherHomeViewModel>()

                        val permissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if (isGranted) {
                                viewModel.fetchLocation()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Location Permission Denied!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                        LaunchedEffect(Unit) {
                            viewModel.locationEvent.collectLatest { location ->
                                navController.navigate(AppDestinations.DetailsDestination(location))
                            }
                        }

                        WeatherHomeScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode },
                            onLocationClick = { locationName ->
                                navController.navigate(
                                    AppDestinations.DetailsDestination(
                                        locationName
                                    )
                                )
                            },
                            onCurrentLocationClick = {
                                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    viewModel.fetchLocation()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }
                        )
                    }
                    composable<AppDestinations.DetailsDestination> { backStackEntry ->
                        val details: AppDestinations.DetailsDestination = backStackEntry.toRoute()
                        val detailViewModel = hiltViewModel<WeatherDetailViewModel>()
                        WeatherDetailScreen(
                            locationName = details.locationName,
                            viewModel = detailViewModel,
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
