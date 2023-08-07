package com.adsama.weatherapp.di

import com.adsama.weatherapp.domain.DeleteLocationUseCase
import com.adsama.weatherapp.domain.FetchCurrentWeatherUseCase
import com.adsama.weatherapp.domain.FetchSaveLocationUseCase
import com.adsama.weatherapp.domain.SaveLocationUseCase
import com.adsama.weatherapp.domain.SearchLocationUseCase
import com.adsama.weatherapp.source.WeatherDataSource
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
    fun provideCurrentWeatherUseCase(weatherDataSource: WeatherDataSource): FetchCurrentWeatherUseCase {
        return FetchCurrentWeatherUseCase(weatherDataSource)
    }

    @Provides
    fun provideSaveLocationUseCase(weatherDataSource: WeatherDataSource): SaveLocationUseCase {
        return SaveLocationUseCase(weatherDataSource)
    }

}