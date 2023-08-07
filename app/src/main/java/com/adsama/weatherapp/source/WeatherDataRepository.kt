package com.adsama.weatherapp.source

import com.adsama.database.PersistedWeatherModel
import com.adsama.network.ResponseWrapper
import com.adsama.weatherapp.source.local.PersistedWeatherSource
import com.adsama.weatherapp.source.remote.RemoteWeatherSource
import javax.inject.Inject


class WeatherDataRepository @Inject constructor(
    private val persistedWeatherSource: PersistedWeatherSource,
    private val remoteWeatherSource: RemoteWeatherSource
) : WeatherDataSource {

    override suspend fun getForecast(location: String, loadForecastCallback: WeatherDataSource.LoadForecastCallback) {
        when (val response = remoteWeatherSource.getWeatherForecast(location)) {
            is ResponseWrapper.Success -> {
                if (response.data.isSuccessful) {
                    loadForecastCallback.onForecastLoaded(response.data.body()!!)
                } else {
                    loadForecastCallback.onError(Throwable("Response body error!"))
                }
            }

            is ResponseWrapper.Failure -> {
                loadForecastCallback.onError(Throwable("Network request failure!"))
            }

            is ResponseWrapper.NetworkError -> {
                loadForecastCallback.onError(Throwable(response.error.error.code.toString() + " " + response.error.error.message!!))
            }
        }
    }

    override suspend fun getSearchResult(location: String, loadSearchCallback: WeatherDataSource.LoadSearchCallback) {
        when (val response = remoteWeatherSource.getSearchResult(location)) {
            is ResponseWrapper.Success -> {
                if (response.data.isSuccessful) {
                    loadSearchCallback.onSearchLoaded(response.data.body()!!)
                } else {
                    loadSearchCallback.onError(Throwable("Response body error!"))
                }
            }

            is ResponseWrapper.Failure -> {
                loadSearchCallback.onError(Throwable("Network request failure!"))
            }

            is ResponseWrapper.NetworkError -> {
                loadSearchCallback.onError(Throwable(response.error.error.code.toString() + " " + response.error.error.message!!))
            }
        }
    }

    override suspend fun getAllSavedLocations(loadSavedCallback: WeatherDataSource.LoadSavedCallback) {
        val savedLocations = persistedWeatherSource.fetchSavedLocations()
        loadSavedCallback.onSavedLoaded(savedLocations)
    }

    override suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel) {
        persistedWeatherSource.saveLocation(persistedWeatherModel)
    }

    override suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel) {
        persistedWeatherSource.deleteLocation(persistedWeatherModel)
    }

}