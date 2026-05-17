package com.adsama.weatherapp.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class WeatherLocationUiModel(
    val id: Long,
    val name: String,
    val region: String,
    val country: String,
    val temperature: String,
    val conditionText: String,
    val conditionIcon: String
)

@Immutable
data class WeatherDetailUiModel(
    val locationName: String,
    val currentTemp: String,
    val conditionText: String,
    val conditionIcon: String,
    val highLowTemp: String,
    val precipitation: String,
    val wind: String,
    val windDir: String,
    val uvIndex: String,
    val sunTimes: String,
    val hourlyForecast: List<HourlyForecastUiModel>,
    val dailyForecast: List<DailyForecastUiModel>,
    val alerts: List<AlertUiModel>
)

@Immutable
data class HourlyForecastUiModel(
    val time: String,
    val temp: String,
    val icon: String,
    val timeEpoch: Int
)

@Immutable
data class DailyForecastUiModel(
    val day: String,
    val date: String,
    val highLowTemp: String,
    val icon: String
)

@Immutable
data class AlertUiModel(
    val event: String,
    val headline: String
)
