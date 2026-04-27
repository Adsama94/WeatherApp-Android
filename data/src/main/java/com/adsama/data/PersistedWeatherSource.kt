package com.adsama.data

import com.adsama.database.PersistedWeatherModel
import com.adsama.database.WeatherLocationDAO
import com.adsama.model.AppError
import com.adsama.model.Result
import javax.inject.Inject

class PersistedWeatherSource @Inject constructor(private val weatherLocationDAO: WeatherLocationDAO) {

    suspend fun fetchSavedLocations(): Result<List<PersistedWeatherModel>> {
        return try {
            val locations = weatherLocationDAO.getAllSavedLocations()
            Result.Success(locations)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError(e.message ?: "Failed to fetch saved locations"))
        }
    }

    suspend fun saveLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit> {
        return try {
            weatherLocationDAO.insertLocationInfo(persistedWeatherModel)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError(e.message ?: "Failed to save location"))
        }
    }

    suspend fun deleteLocation(persistedWeatherModel: PersistedWeatherModel): Result<Unit> {
        return try {
            weatherLocationDAO.deleteLocationInfo(persistedWeatherModel)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.DatabaseError(e.message ?: "Failed to delete location"))
        }
    }

}