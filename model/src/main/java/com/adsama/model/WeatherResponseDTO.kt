package com.adsama.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    @SerialName("alerts")
    val alerts: Alerts = Alerts(emptyList()),
    @SerialName("current")
    val current: Current,
    @SerialName("forecast")
    val forecast: Forecast,
    @SerialName("location")
    val location: Location
)

@Serializable
data class Current(
    @SerialName("condition")
    val condition: Condition,
    @SerialName("feelslike_c")
    val feelslike_c: Double,
    @SerialName("precip_mm")
    val precip_mm: Double,
    @SerialName("temp_c")
    val temp_c: Double,
    @SerialName("uv")
    val uv: Double,
    @SerialName("wind_dir")
    val wind_dir: String,
    @SerialName("wind_kph")
    val wind_kph: Double
)

@Serializable
data class Forecast(
    @SerialName("forecastday")
    val forecastday: List<ForecastDay>
)

@Serializable
data class Location(
    @SerialName("country")
    val country: String? = "",
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
    @SerialName("name")
    val name: String? = "",
    @SerialName("region")
    val region: String? = "",
)

@Serializable
data class Condition(
    @SerialName("icon")
    val icon: String,
    @SerialName("text")
    val text: String
)

@Serializable
data class ForecastDay(
    @SerialName("astro")
    val astro: Astro? = null,
    @SerialName("date")
    val date: String,
    @SerialName("day")
    val day: Day,
    @SerialName("hour")
    val hour: List<Hour> = emptyList()
)

@Serializable
data class Astro(
    @SerialName("sunrise")
    val sunrise: String = "",
    @SerialName("sunset")
    val sunset: String = ""
)

@Serializable
data class Day(
    @SerialName("condition")
    val condition: Condition,
    @SerialName("maxtemp_c")
    val maxtemp_c: Double,
    @SerialName("mintemp_c")
    val mintemp_c: Double
)

@Serializable
data class Hour(
    @SerialName("condition")
    val condition: Condition,
    @SerialName("temp_c")
    val temp_c: Double,
    @SerialName("time")
    val time: String,
    @SerialName("time_epoch")
    val time_epoch: Int
)

@Serializable
data class Alerts(
    @SerialName("alert")
    val alert: List<Alert> = emptyList()
)

@Serializable
data class Alert(
    @SerialName("headline")
    val headline: String? = null,
    @SerialName("event")
    val event: String? = null,
    @SerialName("effective")
    val effective: String? = null,
    @SerialName("expires")
    val expires: String? = null,
    @SerialName("desc")
    val desc: String? = null
)