package com.adsama.domain

import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow

interface WeatherDataSource {

    suspend fun getForecast(location: String): Result<WeatherReport>

    suspend fun getCachedForecast(location: String): Result<WeatherLocation>

    suspend fun getSearchResult(location: String): Result<List<WeatherLocation>>

    fun getAllSavedLocations(): Flow<Result<List<WeatherLocation>>>

    suspend fun saveLocation(location: WeatherLocation): Result<Unit>

    suspend fun deleteLocation(locationId: Long): Result<Unit>

}
