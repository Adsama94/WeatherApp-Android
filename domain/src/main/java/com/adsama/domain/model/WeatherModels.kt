package com.adsama.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherLocation(
    val id: Long = 0,
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double? = null,
    val conditionText: String? = null,
    val conditionIcon: String? = null,
    val lastUpdated: String? = null,
    val lastUpdatedEpoch: Long = 0,
    @kotlinx.serialization.Transient
    val report: WeatherReport? = null
)

@Serializable
data class WeatherReport(
    val location: WeatherLocation,
    val current: CurrentWeather,
    val forecast: List<ForecastDay>,
    val alerts: List<WeatherAlert>
)

@Serializable
data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double,
    val conditionText: String,
    val conditionIcon: String,
    val windKph: Double,
    val windDir: String,
    val precipMm: Double,
    val uv: Double
)

@Serializable
data class ForecastDay(
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val conditionText: String,
    val conditionIcon: String,
    val sunrise: String,
    val sunset: String,
    val hourly: List<HourlyWeather>
)

@Serializable
data class HourlyWeather(
    val time: String,
    val timeEpoch: Int,
    val tempC: Double,
    val conditionText: String,
    val conditionIcon: String
)

@Serializable
data class WeatherAlert(
    val headline: String,
    val description: String,
    val event: String? = null
)
