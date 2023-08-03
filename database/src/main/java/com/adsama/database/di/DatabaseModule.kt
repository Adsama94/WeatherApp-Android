package com.adsama.database.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.adsama.database.WeatherDatabase
import com.adsama.database.WeatherLocationDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherLocationDao(weatherDatabase: WeatherDatabase): WeatherLocationDAO {
        return weatherDatabase.weatherLocationDAO()
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        application: Application
    ): WeatherDatabase {
        return Room
            .databaseBuilder(application, WeatherDatabase::class.java, "weather_db")
            .build()
    }
}