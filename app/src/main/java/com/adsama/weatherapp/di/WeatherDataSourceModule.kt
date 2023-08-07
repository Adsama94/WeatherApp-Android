package com.adsama.weatherapp.di

import com.adsama.database.WeatherLocationDAO
import com.adsama.network.WeatherApi
import com.adsama.weatherapp.source.WeatherDataRepository
import com.adsama.weatherapp.source.WeatherDataSource
import com.adsama.weatherapp.source.local.PersistedWeatherSource
import com.adsama.weatherapp.source.remote.RemoteWeatherSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherDataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteWeatherSource(weatherApi: WeatherApi): RemoteWeatherSource {
        return RemoteWeatherSource(weatherApi)
    }

    @Provides
    @Singleton
    fun providePersistedWeatherSource(weatherLocationDAO: WeatherLocationDAO): PersistedWeatherSource {
        return PersistedWeatherSource(weatherLocationDAO)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(persistedWeatherSource: PersistedWeatherSource, remoteWeatherSource: RemoteWeatherSource): WeatherDataRepository {
        return WeatherDataRepository(persistedWeatherSource, remoteWeatherSource)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
abstract class WeatherModule {

    @Binds
    abstract fun bindWeatherModule(weatherDataRepository: WeatherDataRepository): WeatherDataSource
}