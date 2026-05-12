package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Immutable
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport

@Immutable
data class HomeUiState(
    val savedLocations: List<WeatherLocation> = emptyList(),
    val searchSuggestions: List<WeatherLocation> = emptyList(),
    val searchQuery: String = "",
    val latLong: String = "",
    val isSearchLoading: Boolean = false,
    val isLocalDataLoading: Boolean = false,
    val error: DomainError? = null,
    val isSearchActive: Boolean = false,
    val refreshingLocationIds: Set<Long> = emptySet(),
    val freshWeatherData: Map<Long, WeatherReport> = emptyMap()
)
