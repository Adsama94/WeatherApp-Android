package com.adsama.weatherapp.ui

import kotlinx.serialization.Serializable

sealed class AppDestinations {
    @Serializable
    data object HomeDestination : AppDestinations()

    @Serializable
    data class DetailsDestination(val locationName: String) : AppDestinations()
}