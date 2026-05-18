package com.adsama.database

import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LocalWeatherDataSourceImpl @Inject constructor(
    private val weatherLocationDAO: WeatherLocationDAO
) : LocalWeatherDataSource {

    override fun fetchSavedLocations(): Flow<Result<List<WeatherLocation>>> {
        return weatherLocationDAO.getAllSavedLocations()
            .map { list ->
                Result.Success(list.map { it.toDomain() }) as Result<List<WeatherLocation>>
            }
            .onStart { emit(Result.Loading<List<WeatherLocation>>()) }
            .catch { e ->
                emit(
                    Result.Error<List<WeatherLocation>>(
                        DomainError.DatabaseError(
                            e.message ?: "Failed to fetch saved locations"
                        )
                    )
                )
            }
    }

    override suspend fun saveLocation(location: WeatherLocation): Result<Unit> {
        return try {
            weatherLocationDAO.insertLocationInfo(location.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainError.DatabaseError(e.message ?: "Failed to save location"))
        }
    }

    override suspend fun deleteLocation(location: WeatherLocation): Result<Unit> {
        return try {
            weatherLocationDAO.deleteLocationInfo(location.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainError.DatabaseError(e.message ?: "Failed to delete location"))
        }
    }

    override suspend fun fetchForecast(location: String): Result<WeatherLocation> {
        return try {
            val savedLocation =
                weatherLocationDAO.getAllSavedLocationsOnce().find { it.name == location }
            if (savedLocation != null) {
                Result.Success(savedLocation.toDomain())
            } else {
                Result.Error(DomainError.DatabaseError("No cached forecast found for $location"))
            }
        } catch (e: Exception) {
            Result.Error(DomainError.DatabaseError(e.message ?: "Failed to fetch cached forecast"))
        }
    }

}

fun PersistedWeatherModel.toDomain(): WeatherLocation {
    return WeatherLocation(
        id = locationId,
        name = name,
        region = region,
        country = country,
        latitude = lat,
        longitude = lon,
        temperature = temp_c,
        conditionText = text,
        conditionIcon = icon,
        lastUpdated = date,
        report = report
    )
}

fun WeatherLocation.toEntity(): PersistedWeatherModel {
    return PersistedWeatherModel(
        locationId = id,
        lat = latitude,
        lon = longitude,
        name = name,
        region = region,
        country = country,
        temp_c = temperature ?: 0.0,
        text = conditionText ?: "",
        icon = conditionIcon ?: "",
        date = lastUpdated ?: "",
        report = report
    )
}
