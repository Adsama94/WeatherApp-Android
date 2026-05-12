package com.adsama.weatherapp.ui.details

import androidx.compose.runtime.Immutable
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.ForecastDay
import com.adsama.domain.model.HourlyWeather
import com.adsama.domain.model.WeatherAlert
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport

@Immutable
data class DetailUiState(
    val forecast: WeatherReport? = null,
    val hourlyForecast: List<HourlyWeather> = emptyList(),
    val fiveDayForecast: List<ForecastDay> = emptyList(),
    val persistedDataList: List<WeatherLocation> = emptyList(),
    val alerts: List<WeatherAlert> = emptyList(),
    val isPersisted: Boolean = false,
    val isLoading: Boolean = false,
    val error: DomainError? = null
)
