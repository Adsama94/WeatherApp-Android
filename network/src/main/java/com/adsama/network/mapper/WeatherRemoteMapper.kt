package com.adsama.network.mapper

import com.adsama.domain.model.*
import com.adsama.model.*
import javax.inject.Inject

class WeatherRemoteMapper @Inject constructor() {

    fun mapForecastResponseToDomain(response: ForecastResponse): WeatherReport {
        return WeatherReport(
            location = mapLocationToDomain(response.location),
            current = mapCurrentToDomain(response.current),
            forecast = response.forecast.forecastday.map { mapForecastDayToDomain(it) },
            alerts = response.alerts.alert.map { mapAlertToDomain(it) }
        )
    }

    fun mapSearchResponseToDomain(response: SearchResponse): WeatherLocation {
        return WeatherLocation(
            name = response.name,
            region = response.region,
            country = response.country,
            latitude = response.lat,
            longitude = response.lon
        )
    }

    private fun mapLocationToDomain(location: Location): WeatherLocation {
        return WeatherLocation(
            name = location.name ?: "",
            region = location.region ?: "",
            country = location.country ?: "",
            latitude = location.lat,
            longitude = location.lon
        )
    }

    private fun mapCurrentToDomain(current: Current): CurrentWeather {
        return CurrentWeather(
            tempC = current.temp_c,
            feelsLikeC = current.feelslike_c,
            conditionText = current.condition.text,
            conditionIcon = current.condition.icon,
            windKph = current.wind_kph,
            windDir = current.wind_dir,
            precipMm = current.precip_mm,
            uv = current.uv
        )
    }

    private fun mapForecastDayToDomain(forecastDay: com.adsama.model.ForecastDay): com.adsama.domain.model.ForecastDay {
        return com.adsama.domain.model.ForecastDay(
            date = forecastDay.date,
            maxTempC = forecastDay.day.maxtemp_c,
            minTempC = forecastDay.day.mintemp_c,
            conditionText = forecastDay.day.condition.text,
            conditionIcon = forecastDay.day.condition.icon,
            sunrise = forecastDay.astro.sunrise,
            sunset = forecastDay.astro.sunset,
            hourly = forecastDay.hour.map { mapHourToDomain(it) }
        )
    }

    private fun mapHourToDomain(hour: Hour): HourlyWeather {
        return HourlyWeather(
            time = hour.time,
            timeEpoch = hour.time_epoch,
            tempC = hour.temp_c,
            conditionText = hour.condition.text,
            conditionIcon = hour.condition.icon
        )
    }

    private fun mapAlertToDomain(alert: Alert): WeatherAlert {
        return WeatherAlert(
            headline = alert.headline ?: "",
            description = alert.desc ?: "",
            event = alert.event
        )
    }
}
