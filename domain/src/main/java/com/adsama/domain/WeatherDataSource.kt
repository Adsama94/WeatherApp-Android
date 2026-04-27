package com.adsama.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.ForecastResponse
import com.adsama.model.Result
import com.adsama.model.SearchResponse

interface WeatherDataSource {

    suspend fun getForecast(location: String): Result<ForecastResponse>

    suspend fun getSearchResult(location: String): Result<List<SearchResponse>>

    suspend fun getAllSavedLocations(): Result<List<PersistedWeatherModel>>

    suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit>

    suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit>

}