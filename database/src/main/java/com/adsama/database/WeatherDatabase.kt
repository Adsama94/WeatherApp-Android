package com.adsama.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private lateinit var INSTANCE: WeatherDatabase

@Database(entities = [PersistedWeatherModel::class], version = 1, exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherLocationDAO(): WeatherLocationDAO
}

fun getWeatherDatabase(application: Application): WeatherDatabase {
    synchronized(WeatherDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                application,
                WeatherDatabase::class.java, "weather_db"
            ).build()
        }
    }
    return INSTANCE
}