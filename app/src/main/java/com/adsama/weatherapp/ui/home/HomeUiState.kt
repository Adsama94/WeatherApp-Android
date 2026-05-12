package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Immutable
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.AppError
import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse

@Immutable
data class HomeUiState(
    val savedLocations: List<PersistedWeatherModel> = emptyList(),
    val searchSuggestions: List<SearchResponse> = emptyList(),
    val searchQuery: String = "",
    val latLong: String = "",
    val isSearchLoading: Boolean = false,
    val isLocalDataLoading: Boolean = false,
    val error: AppError? = null,
    val isSearchActive: Boolean = false,
    val refreshingLocationIds: Set<Long> = emptySet(),
    val freshWeatherData: Map<Long, ForecastResponse> = emptyMap()
)
