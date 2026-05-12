package com.adsama.data

import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherDataRepository @Inject constructor(
    private val persistedWeatherSource: PersistedWeatherSource,
    private val remoteWeatherSource: RemoteWeatherSource
) : WeatherDataSource {

    override suspend fun getForecast(location: String): Result<WeatherReport> {
        return remoteWeatherSource.getWeatherForecast(location)
    }

    override suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> {
        return remoteWeatherSource.getSearchResult(location)
    }

    override fun getAllSavedLocations(): Flow<Result<List<WeatherLocation>>> {
        return persistedWeatherSource.fetchSavedLocations()
    }

    override suspend fun saveLocation(location: WeatherLocation): Result<Unit> {
        return persistedWeatherSource.saveLocation(location)
    }

    override suspend fun deleteLocation(location: WeatherLocation): Result<Unit> {
        return persistedWeatherSource.deleteLocation(location)
    }

}
