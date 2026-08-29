package com.adsama.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow

interface LocalWeatherDataSource {
    fun fetchSavedLocations(): Flow<Result<List<WeatherLocation>>>
    fun getPagingSavedLocations(config: PagingConfig): Flow<PagingData<WeatherLocation>>
    suspend fun saveLocation(location: WeatherLocation): Result<Unit>
    suspend fun saveLocations(locations: List<WeatherLocation>): Result<Unit>
    suspend fun deleteLocation(locationId: Long): Result<Unit>
    suspend fun fetchForecast(location: String): Result<WeatherLocation>
}

interface RemoteWeatherDataSource {
    suspend fun getSearchResult(location: String): Result<List<WeatherLocation>>
    suspend fun getWeatherForecast(location: String): Result<WeatherReport>
}
