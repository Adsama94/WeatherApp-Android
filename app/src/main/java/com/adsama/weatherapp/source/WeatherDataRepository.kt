package com.adsama.weatherapp.source

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse
import com.adsama.network.ResponseWrapper
import com.adsama.weatherapp.source.local.PersistedWeatherSource
import com.adsama.weatherapp.source.remote.RemoteWeatherSource
import javax.inject.Inject


private lateinit var INSTANCE: WeatherDataRepository

class WeatherDataRepository @Inject constructor(
    private val persistedWeatherSource: PersistedWeatherSource,
    private val remoteWeatherSource: RemoteWeatherSource
) : WeatherDataSource {

    companion object {
        fun getInstance(
            persistedWeatherSource: PersistedWeatherSource,
            remoteWeatherSource: RemoteWeatherSource
        ): WeatherDataRepository {
            synchronized(WeatherDataRepository::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = WeatherDataRepository(persistedWeatherSource, remoteWeatherSource)
                }
                return INSTANCE
            }
        }
    }

    override suspend fun getForecast(location: String): ForecastResponse {
        return when (val response = remoteWeatherSource.getWeatherForecast(location)) {
            is ResponseWrapper.Success -> {
                response.data.body() ?: throw RuntimeException("Response body is null")
            }

            is ResponseWrapper.Failure -> {
                throw Throwable("Network request failure!")
            }

            is ResponseWrapper.NetworkError -> {
                throw Throwable(response.error.error.code.toString() + " " + response.error.error.message!!)
            }
        }
    }

    override suspend fun getSearchResult(location: String): List<SearchResponse> {
        return when (val response = remoteWeatherSource.getSearchResult(location)) {
            is ResponseWrapper.Success -> {
                response.data.body() ?: throw RuntimeException("Response body is null")
            }

            is ResponseWrapper.Failure -> {
                throw Throwable("Network request failure!")
            }

            is ResponseWrapper.NetworkError -> {
                throw Throwable(response.error.error.code.toString() + " " + response.error.error.message!!)
            }
        }
    }

    override suspend fun getAllSavedLocations(): List<PersistedWeatherModel> {
        return persistedWeatherSource.fetchSavedLocations()
    }

    override suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel): String {
        persistedWeatherSource.saveLocation(persistedWeatherModel)
        return "Data inserted successfully"
    }

    override suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel): String {
        persistedWeatherSource.deleteLocation(persistedWeatherModel)
        return "Data deleted successfully"
    }

}