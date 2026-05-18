package com.adsama.model

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)

@Serializable
data class Current(
    val condition: Condition,
    val feelslike_c: Double,
    val precip_mm: Double,
    val temp_c: Double,
    val uv: Double,
    val wind_dir: String,
    val wind_kph: Double
)

@Serializable
data class Forecast(
    val forecastday: List<ForecastDay>
)

@Serializable
data class Location(
    val country: String? = "",
    val lat: Double,
    val lon: Double,
    val name: String? = "",
    val region: String? = "",
)

@Serializable
data class Condition(
    val icon: String,
    val text: String
)

@Serializable
data class ForecastDay(
    val astro: Astro,
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

@Serializable
data class Astro(
    val sunrise: String,
    val sunset: String
)

@Serializable
data class Day(
    val condition: Condition,
    val maxtemp_c: Double,
    val mintemp_c: Double
)

@Serializable
data class Hour(
    val condition: Condition,
    val temp_c: Double,
    val time: String,
    val time_epoch: Int
)

@Serializable
data class Alerts(
    val alert: List<Alert>
)

@Serializable
data class Alert(
    val headline: String? = null,
    val event: String? = null,
    val effective: String? = null,
    val expires: String? = null,
    val desc: String? = null
)