package com.adsama.data

import com.adsama.database.WeatherLocationDAO
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class PersistedWeatherSource @Inject constructor(private val weatherLocationDAO: WeatherLocationDAO) {

    fun fetchSavedLocations(): Flow<Result<List<WeatherLocation>>> {
        return weatherLocationDAO.getAllSavedLocations()
            .map { list ->
                Result.Success(list.map { it.toDomain() }) as Result<List<WeatherLocation>>
            }
            .onStart { emit(Result.Loading()) }
            .catch { e ->
                emit(
                    Result.Error(
                        DomainError.DatabaseError(
                            e.message ?: "Failed to fetch saved locations"
                        )
                    )
                )
            }
    }

    suspend fun saveLocation(location: WeatherLocation): Result<Unit> {
        return try {
            weatherLocationDAO.insertLocationInfo(location.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainError.DatabaseError(e.message ?: "Failed to save location"))
        }
    }

    suspend fun deleteLocation(location: WeatherLocation): Result<Unit> {
        return try {
            weatherLocationDAO.deleteLocationInfo(location.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainError.DatabaseError(e.message ?: "Failed to delete location"))
        }
    }

}
