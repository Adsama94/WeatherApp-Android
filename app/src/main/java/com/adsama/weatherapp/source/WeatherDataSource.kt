package com.adsama.weatherapp.source

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse

interface WeatherDataSource {

    suspend fun getForecast(location: String) : ForecastResponse

    suspend fun getSearchResult(location: String) : List<SearchResponse>

    suspend fun getAllSavedLocations(): List<PersistedWeatherModel>

    suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel) : String

    suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel): String

}