package com.adsama.data

import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow

interface LocalWeatherDataSource {
    fun fetchSavedLocations(): Flow<Result<List<WeatherLocation>>>
    suspend fun saveLocation(location: WeatherLocation): Result<Unit>
    suspend fun deleteLocation(location: WeatherLocation): Result<Unit>
    suspend fun fetchForecast(location: String): Result<WeatherLocation>
}

interface RemoteWeatherDataSource {
    suspend fun getSearchResult(location: String): Result<List<WeatherLocation>>
    suspend fun getWeatherForecast(location: String): Result<WeatherReport>
}