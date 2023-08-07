package com.adsama.weatherapp.source

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse

interface WeatherDataSource {

    suspend fun getForecast(location: String, loadForecastCallback: LoadForecastCallback)

    suspend fun getSearchResult(location: String, loadSearchCallback: LoadSearchCallback)

    suspend fun getAllSavedLocations(loadSavedCallback: LoadSavedCallback)

    suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel)

    suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel)

    interface LoadForecastCallback {
        fun onForecastLoaded(forecastResponse: ForecastResponse)
        fun onError(t: Throwable)
    }

    interface LoadSearchCallback {
        fun onSearchLoaded(searchResponse: List<SearchResponse>)
        fun onError(t: Throwable)
    }

    interface LoadSavedCallback {
        fun onSavedLoaded(savedList: List<PersistedWeatherModel>)
        fun onError(t: Throwable)
    }

}