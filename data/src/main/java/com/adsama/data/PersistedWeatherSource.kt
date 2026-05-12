package com.adsama.data

import com.adsama.database.PersistedWeatherModel
import com.adsama.database.WeatherLocationDAO
import com.adsama.model.AppError
import com.adsama.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class PersistedWeatherSource @Inject constructor(private val weatherLocationDAO: WeatherLocationDAO) {

    fun fetchSavedLocations(): Flow<Result<List<PersistedWeatherModel>>> {
        return weatherLocationDAO.getAllSavedLocations()
            .map { Result.Success(it) as Result<List<PersistedWeatherModel>> }
            .onStart { emit(Result.Loading()) }
            .catch { e ->
                emit(
                    Result.Error(
                        AppError.DatabaseError(
                            e.message ?: "Failed to fetch saved locations"
                        )
                    )
                )
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