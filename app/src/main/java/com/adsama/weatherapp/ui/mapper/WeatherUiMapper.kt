package com.adsama.weatherapp.ui.mapper

import com.adsama.domain.model.*
import com.adsama.weatherapp.ui.model.*
import com.adsama.weatherapp.utils.setDayFromDate
import com.adsama.weatherapp.utils.setFormattedDate

fun WeatherLocation.toUiModel(
    freshReport: WeatherReport? = null,
    isRefreshing: Boolean = false
): WeatherLocationUiModel {
    val temp = freshReport?.current?.tempC ?: this.temperature ?: 0.0
    val condition = freshReport?.current?.conditionText ?: this.conditionText ?: ""
    val icon = freshReport?.current?.conditionIcon ?: this.conditionIcon ?: ""
    
    return WeatherLocationUiModel(
        id = id,
        name = name,
        region = region,
        country = country,
        temperature = "${temp.toInt()}°C",
        conditionText = condition,
        conditionIcon = icon.ensureHttpsPrefix(),
        isRefreshing = isRefreshing
    )
}

fun WeatherReport.toDetailUiModel(): WeatherDetailUiModel {
    val firstDay = forecast.getOrNull(0)
    return WeatherDetailUiModel(
        locationName = location.name,
        currentTemp = "${current.tempC.toInt()}°",
        conditionText = current.conditionText,
        conditionIcon = current.conditionIcon.ensureHttpsPrefix(),
        highLowTemp = if (firstDay != null) "H:${firstDay.maxTempC.toInt()}°  L:${firstDay.minTempC.toInt()}°" else "",
        precipitation = "${current.precipMm} mm",
        wind = "${current.windKph} kph",
        windDir = current.windDir,
        uvIndex = current.uv.toString(),
        sunTimes = if (firstDay != null) "↑ ${firstDay.sunrise}\n↓ ${firstDay.sunset}" else "",
        hourlyForecast = forecast.take(2).flatMap { it.hourly }.map { it.toUiModel() },
        dailyForecast = forecast.map { it.toUiModel() },
        alerts = alerts.map { it.toUiModel() }
    )
}

fun HourlyWeather.toUiModel(): HourlyForecastUiModel {
    return HourlyForecastUiModel(
        time = time.split(" ").getOrElse(1) { "" },
        temp = "${tempC.toInt()}°",
        icon = conditionIcon.ensureHttpsPrefix(),
        timeEpoch = timeEpoch
    )
}

fun ForecastDay.toUiModel(): DailyForecastUiModel {
    return DailyForecastUiModel(
        day = setDayFromDate(date),
        date = setFormattedDate(date) ?: "",
        highLowTemp = "${maxTempC.toInt()}° / ${minTempC.toInt()}°",
        icon = conditionIcon.ensureHttpsPrefix()
    )
}

fun WeatherAlert.toUiModel(): AlertUiModel {
    return AlertUiModel(
        event = event ?: "Alert",
        headline = headline
    )
}

private fun String.ensureHttpsPrefix(): String {
    return if (this.startsWith("//")) "https:$this" else this
}
