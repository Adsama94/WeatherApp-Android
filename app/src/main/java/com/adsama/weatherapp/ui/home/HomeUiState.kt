package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Immutable
import com.adsama.domain.model.DomainError
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel

@Immutable
data class HomeUiState(
    val searchSuggestions: List<WeatherLocationUiModel> = emptyList(),
    val latLong: String = "",
    val isSearchLoading: Boolean = false,
    val error: DomainError? = null,
    val isSearchActive: Boolean = false
)
