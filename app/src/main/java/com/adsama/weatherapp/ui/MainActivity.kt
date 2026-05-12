package com.adsama.weatherapp.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
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
import androidx.core.content.ContextCompat.startForegroundService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adsama.weatherapp.ui.details.WeatherDetailScreen
import com.adsama.weatherapp.ui.details.WeatherDetailViewModel
import com.adsama.weatherapp.ui.home.WeatherHomeScreen
import com.adsama.weatherapp.ui.home.WeatherHomeViewModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import com.adsama.weatherapp.utils.LocationCallbacks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), LocationCallbacks {

    private var isServiceBound = false
    private var locationService: WeatherLocationService? = null
    private var pendingLocationUpdate = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as WeatherLocationService.LocalBinder
            locationService = binder.getService()
            locationService?.setCallback(this@MainActivity)
            isServiceBound = true
            if (pendingLocationUpdate) {
                startLocationService()
                pendingLocationUpdate = false
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            locationService = null
        }
    }

    private var latitudeState by mutableStateOf<Double?>(null)
    private var longitudeState by mutableStateOf<Double?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemInDarkTheme) }

            WeatherAppTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        extractLocation()
                    } else {
                        Toast.makeText(this, "Location Permission Denied!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val viewModel = hiltViewModel<WeatherHomeViewModel>()

                        LaunchedEffect(latitudeState, longitudeState) {
                            val lat = latitudeState
                            val lon = longitudeState
                            if (lat != null && lon != null) {
                                viewModel.setLocationFromGps(lat, lon)
                                latitudeState = null
                                longitudeState = null
                            }
                        }

                        WeatherHomeScreen(
                            viewModel = viewModel,
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode },
                            onLocationClick = { locationName ->
                                navController.navigate("details/$locationName")
                            },
                            onCurrentLocationClick = {
                                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    extractLocation()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }
                        )
                    }
                    composable(
                        route = "details/{locationName}",
                        arguments = listOf(navArgument("locationName") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val locationName = backStackEntry.arguments?.getString("locationName") ?: ""
                        val detailViewModel = hiltViewModel<WeatherDetailViewModel>()
                        WeatherDetailScreen(
                            locationName = locationName,
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

    private fun extractLocation() {
        if (!isServiceBound) {
            val locationServiceIntent = Intent(this, WeatherLocationService::class.java)
            bindService(locationServiceIntent, serviceConnection, BIND_AUTO_CREATE)
            pendingLocationUpdate = true
        } else {
            startLocationService()
        }
    }

    private fun startLocationService() {
        val locationServiceIntent = Intent(this, WeatherLocationService::class.java)
        locationServiceIntent.action = WeatherLocationService.ACTION_GET_LOCATION_ONCE
        startForegroundService(this, locationServiceIntent)
    }

    override fun getLocation(latitude: Double, longitude: Double) {
        latitudeState = latitude
        longitudeState = longitude
        Toast.makeText(this, "Location updated: $latitude, $longitude", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}
