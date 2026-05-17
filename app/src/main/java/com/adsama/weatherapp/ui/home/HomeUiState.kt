package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Immutable
import com.adsama.domain.model.DomainError
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel

@Immutable
data class HomeUiState(
    val savedLocations: List<WeatherLocationUiModel> = emptyList(),
    val refreshingLocationIds: Set<Long> = emptySet(),
    val searchSuggestions: List<WeatherLocationUiModel> = emptyList(),
    val searchQuery: String = "",
    val latLong: String = "",
    val isSearchLoading: Boolean = false,
    val isLocalDataLoading: Boolean = false,
    val error: DomainError? = null,
    val isSearchActive: Boolean = false
)
