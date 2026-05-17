package com.adsama.weatherapp.ui.details

import androidx.compose.runtime.Immutable
import com.adsama.domain.model.DomainError
import com.adsama.weatherapp.ui.model.WeatherDetailUiModel

@Immutable
data class DetailUiState(
    val weather: WeatherDetailUiModel? = null,
    val isPersisted: Boolean = false,
    val isLoading: Boolean = false,
    val error: DomainError? = null
)
