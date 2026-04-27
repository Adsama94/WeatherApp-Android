package com.adsama.data

import com.adsama.database.WeatherLocationDAO
import com.adsama.domain.WeatherDataSource
import com.adsama.network.WeatherApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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
    fun provideWeatherRepository(
        persistedWeatherSource: PersistedWeatherSource,
        remoteWeatherSource: RemoteWeatherSource
    ): WeatherDataRepository {
        return WeatherDataRepository(persistedWeatherSource, remoteWeatherSource)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherDataSource(weatherDataRepository: WeatherDataRepository): WeatherDataSource
}