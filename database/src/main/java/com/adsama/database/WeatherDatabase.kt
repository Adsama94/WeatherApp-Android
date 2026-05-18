package com.adsama.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PersistedWeatherModel::class], version = 4, exportSchema = true)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherLocationDAO(): WeatherLocationDAO
}