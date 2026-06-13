package com.adsama.weatherapp.di

import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.domain.TimeProvider
import com.adsama.domain.WeatherDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    fun provideSearchLocationUseCase(weatherDataSource: WeatherDataSource): SearchLocationUseCase {
        return SearchLocationUseCase(weatherDataSource)
    }

    @Provides
    fun provideFetchSaveLocationUseCase(weatherDataSource: WeatherDataSource): FetchSaveLocationUseCase {
        return FetchSaveLocationUseCase(weatherDataSource)
    }

    @Provides
    fun provideDeleteLocationUseCase(weatherDataSource: WeatherDataSource): DeleteLocationUseCase {
        return DeleteLocationUseCase(weatherDataSource)
    }

    @Provides
    fun provideCurrentWeatherUseCase(
        weatherDataSource: WeatherDataSource,
        timeProvider: TimeProvider
    ): FetchCurrentWeatherUseCase {
        return FetchCurrentWeatherUseCase(weatherDataSource, timeProvider)
    }

    @Provides
    fun provideSaveLocationUseCase(weatherDataSource: WeatherDataSource): SaveLocationUseCase {
        return SaveLocationUseCase(weatherDataSource)
    }

}