package com.adsama.data

import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.WeatherDataSource
import com.adsama.model.ForecastResponse
import com.adsama.model.Result
import com.adsama.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherDataRepository @Inject constructor(
    private val persistedWeatherSource: PersistedWeatherSource,
    private val remoteWeatherSource: RemoteWeatherSource
) : WeatherDataSource {

    override suspend fun getForecast(location: String): Result<ForecastResponse> {
        return remoteWeatherSource.getWeatherForecast(location)
    }

    override suspend fun getSearchResult(location: String): Result<List<SearchResponse>> {
        return remoteWeatherSource.getSearchResult(location)
    }

    override fun getAllSavedLocations(): Flow<Result<List<PersistedWeatherModel>>> {
        return persistedWeatherSource.fetchSavedLocations()
    }

    override suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit> {
        return persistedWeatherSource.saveLocation(persistedWeatherModel)
    }

    override suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit> {
        return persistedWeatherSource.deleteLocation(persistedWeatherModel)
    }

}