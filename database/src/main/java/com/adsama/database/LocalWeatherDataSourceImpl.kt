package com.adsama.database

import com.adsama.domain.DispatcherProvider
import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

class LocalWeatherDataSourceImpl @Inject constructor(
    private val weatherLocationDAO: WeatherLocationDAO,
    private val dispatcherProvider: DispatcherProvider
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

    override suspend fun saveLocation(location: WeatherLocation): Result<Unit> =
        withContext(dispatcherProvider.io) {
            weatherLocationDAO.insertLocationInfo(location.toEntity())
            Result.Success(Unit)
        }

    override suspend fun deleteLocation(locationId: Long): Result<Unit> =
        withContext(dispatcherProvider.io) {
            weatherLocationDAO.deleteLocationById(locationId)
            Result.Success(Unit)
        }

    override suspend fun fetchForecast(location: String): Result<WeatherLocation> =
        withContext(dispatcherProvider.io) {
            val allSaved = weatherLocationDAO.getAllSavedLocationsOnce()
            val savedLocation = allSaved.find {
                val compositeName = "${it.name}, ${it.region}, ${it.country}"
                compositeName.equals(location, ignoreCase = true) || it.name.equals(
                    location,
                    ignoreCase = true
                )
            } ?: allSaved.find {
                val coordinates = location.split(",")
                if (coordinates.size == 2) {
                    val lat = coordinates[0].trim().toDoubleOrNull()
                    val lon = coordinates[1].trim().toDoubleOrNull()
                    if (lat != null && lon != null) {
                        // Match with small epsilon for coordinates
                        abs(it.lat - lat) < 0.001 && abs(it.lon - lon) < 0.001
                    } else false
                } else false
            }

            if (savedLocation != null) {
                Result.Success(savedLocation.toDomain())
            } else {
                Result.Error(DomainError.DatabaseError("No cached forecast found for $location"))
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
        lastUpdatedEpoch = lastUpdatedEpoch,
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
        lastUpdatedEpoch = lastUpdatedEpoch,
        report = report
    )
}
