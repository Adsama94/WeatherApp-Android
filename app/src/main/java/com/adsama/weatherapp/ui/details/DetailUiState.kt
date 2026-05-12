package com.adsama.weatherapp.ui.details

import androidx.compose.runtime.Immutable
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.Alert
import com.adsama.model.AppError
import com.adsama.model.ForecastDay
import com.adsama.model.ForecastResponse
import com.adsama.model.Hour

@Immutable
data class DetailUiState(
    val forecast: ForecastResponse? = null,
    val hourlyForecast: List<Hour> = emptyList(),
    val fiveDayForecast: List<ForecastDay> = emptyList(),
    val persistedDataList: List<PersistedWeatherModel> = emptyList(),
    val alerts: List<Alert> = emptyList(),
    val isPersisted: Boolean = false,
    val isLoading: Boolean = false,
    val error: AppError? = null
)
