package com.adsama.database

import androidx.room.TypeConverter
import com.adsama.domain.model.WeatherReport
import kotlinx.serialization.json.Json

class WeatherTypeConverters {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromWeatherReport(report: WeatherReport?): String? {
        return if (report == null) null else json.encodeToString(report)
    }

    @TypeConverter
    fun toWeatherReport(jsonString: String?): WeatherReport? {
        return if (jsonString.isNullOrEmpty()) null else json.decodeFromString<WeatherReport>(
            jsonString
        )
    }
}
