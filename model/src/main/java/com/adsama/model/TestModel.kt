package com.adsama.model

data class ForecastResponse(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)

data class Current(
    val condition: Condition,
    val feelslike_c: Double,
    val precip_mm: Double,
    val temp_c: Double,
    val uv: Double,
    val wind_dir: String,
    val wind_kph: Double
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class Location(
    val country: String? = "",
    val lat: Double,
    val lon: Double,
    val name: String? = "",
    val region: String? = "",
)

data class Condition(
    val icon: String,
    val text: String
)

data class ForecastDay(
    val astro: Astro,
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Astro(
    val sunrise: String,
    val sunset: String
)

data class Day(
    val condition: Condition,
    val maxtemp_c: Double,
    val mintemp_c: Double
)

data class Hour(
    val condition: Condition,
    val temp_c: Double,
    val time: String,
    val time_epoch: Int
)

data class Alerts(
    val alert: List<Alert>
)

data class Alert(
    val headline: String? = null,
    val event: String? = null,
    val effective: String? = null,
    val expires: String? = null,
    val desc: String? = null
)