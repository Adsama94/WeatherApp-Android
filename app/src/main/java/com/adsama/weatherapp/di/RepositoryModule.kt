package com.adsama.weatherapp.di

import com.adsama.data.DefaultTimeProvider
import com.adsama.data.WeatherDataRepository
import com.adsama.domain.DefaultDispatcherProvider
import com.adsama.domain.DispatcherProvider
import com.adsama.domain.TimeProvider
import com.adsama.domain.WeatherDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherDataSource(weatherDataRepository: WeatherDataRepository): WeatherDataSource

    @Binds
    @Singleton
    abstract fun bindTimeProvider(defaultTimeProvider: DefaultTimeProvider): TimeProvider

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}
