package com.adsama.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PersistedWeatherModel::class], version = 1, exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherLocationDAO(): WeatherLocationDAO
}