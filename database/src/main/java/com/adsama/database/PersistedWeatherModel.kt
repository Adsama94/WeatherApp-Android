package com.adsama.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adsama.domain.model.WeatherReport

@Entity
data class PersistedWeatherModel(
    @PrimaryKey(autoGenerate = true) var locationId: Long = 0,
    @ColumnInfo(name = "latitude") val lat: Double,
    @ColumnInfo(name = "longitude") val lon: Double,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "region") val region: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "temperature") val temp_c: Double,
    @ColumnInfo(name = "text_desc") val text: String,
    @ColumnInfo(name = "icon_weather") val icon: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "last_updated_epoch") val lastUpdatedEpoch: Long = 0,
    @ColumnInfo(name = "weather_report") val report: WeatherReport? = null
)