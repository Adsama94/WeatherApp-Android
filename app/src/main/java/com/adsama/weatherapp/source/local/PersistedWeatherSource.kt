package com.adsama.weatherapp.source.local

import com.adsama.database.PersistedWeatherModel
import com.adsama.database.WeatherLocationDAO
import javax.inject.Inject

class PersistedWeatherSource @Inject constructor(weatherLocationDAO: WeatherLocationDAO) {

    private val mWeatherLocationDAO = weatherLocationDAO

    suspend fun fetchSavedLocations(): List<PersistedWeatherModel> {
        return mWeatherLocationDAO.getAllSavedLocations()
    }

    suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel) {
        mWeatherLocationDAO.insertLocationInfo(persistedWeatherModel)
    }

    suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel) {
        mWeatherLocationDAO.deleteLocationInfo(persistedWeatherModel)
    }

}