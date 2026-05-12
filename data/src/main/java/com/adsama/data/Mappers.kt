package com.adsama.data

import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.model.CurrentWeather
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.HourlyWeather
import com.adsama.domain.model.WeatherAlert
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.model.Alert
import com.adsama.model.AppError
import com.adsama.model.Current
import com.adsama.model.ForecastDay
import com.adsama.model.ForecastResponse
import com.adsama.model.Hour
import com.adsama.model.Location
import com.adsama.model.SearchResponse

fun ForecastResponse.toDomain(): WeatherReport {
    return WeatherReport(
        location = location.toDomain(),
        current = current.toDomain(),
        forecast = forecast.forecastday.map { it.toDomain() },
        alerts = alerts.alert.map { it.toDomain() }
    )
}

fun Location.toDomain(): WeatherLocation {
    return WeatherLocation(
        name = name ?: "",
        region = region ?: "",
        country = country ?: "",
        latitude = lat,
        longitude = lon
    )
}

fun Current.toDomain(): CurrentWeather {
    return CurrentWeather(
        tempC = temp_c,
        feelsLikeC = feelslike_c,
        conditionText = condition.text,
        conditionIcon = condition.icon,
        windKph = wind_kph,
        windDir = wind_dir,
        precipMm = precip_mm,
        uv = uv
    )
}

fun ForecastDay.toDomain(): com.adsama.domain.model.ForecastDay {
    return com.adsama.domain.model.ForecastDay(
        date = date,
        maxTempC = day.maxtemp_c,
        minTempC = day.mintemp_c,
        conditionText = day.condition.text,
        conditionIcon = day.condition.icon,
        sunrise = astro.sunrise,
        sunset = astro.sunset,
        hourly = hour.map { it.toDomain() }
    )
}

fun Hour.toDomain(): HourlyWeather {
    return HourlyWeather(
        time = time,
        timeEpoch = time_epoch,
        tempC = temp_c,
        conditionText = condition.text,
        conditionIcon = condition.icon
    )
}

fun Alert.toDomain(): WeatherAlert {
    return WeatherAlert(
        headline = headline ?: "",
        description = desc ?: "",
        event = event
    )
}

fun SearchResponse.toDomain(): WeatherLocation {
    return WeatherLocation(
        name = name,
        region = region,
        country = country,
        latitude = lat,
        longitude = lon
    )
}

fun PersistedWeatherModel.toDomain(): WeatherLocation {
    return WeatherLocation(
        id = locationId,
        name = name,
        region = region,
        country = country,
        latitude = lat,
        longitude = lon,
        temperature = temp_c,
        conditionText = text,
        conditionIcon = icon,
        lastUpdated = date
    )
}

fun WeatherLocation.toEntity(): PersistedWeatherModel {
    return PersistedWeatherModel(
        locationId = id,
        lat = latitude,
        lon = longitude,
        name = name,
        region = region,
        country = country,
        temp_c = temperature ?: 0.0,
        text = conditionText ?: "",
        icon = conditionIcon ?: "",
        date = lastUpdated ?: ""
    )
}

fun Throwable.toDomainError(): DomainError {
    return when (this) {
        is AppError.NetworkError -> DomainError.NetworkError(message ?: "Network error")
        is AppError.ApiError -> DomainError.ApiError(code, message ?: "API error")
        is AppError.DatabaseError -> DomainError.DatabaseError(message ?: "Database error")
        is AppError.ValidationError -> DomainError.ValidationError(message ?: "Validation error")
        else -> DomainError.UnknownError(message ?: "Unknown error occurred")
    }
}
