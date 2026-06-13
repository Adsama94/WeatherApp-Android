package com.adsama.data

import com.adsama.domain.DispatcherProvider
import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.RemoteWeatherDataSource
import com.adsama.domain.TimeProvider
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherDataRepository @Inject constructor(
    private val localWeatherDataSource: LocalWeatherDataSource,
    private val remoteWeatherDataSource: RemoteWeatherDataSource,
    private val timeProvider: TimeProvider,
    private val dispatcherProvider: DispatcherProvider
) : WeatherDataSource {

    override suspend fun getForecast(
        location: String
    ): Result<WeatherReport> = withContext(dispatcherProvider.io) {
        val localResult = localWeatherDataSource.fetchForecast(location)

        // Fetch from remote
        val remoteResult = remoteWeatherDataSource.getWeatherForecast(location)
        if (remoteResult is Result.Success) {
            val freshReport = remoteResult.data
            // Only update/save if it's already in our saved list
            if (localResult is Result.Success) {
                val existingLocation = localResult.data
                localWeatherDataSource.saveLocation(
                    existingLocation.copy(
                        temperature = freshReport.current.tempC,
                        conditionText = freshReport.current.conditionText,
                        conditionIcon = freshReport.current.conditionIcon,
                        lastUpdatedEpoch = timeProvider.getCurrentTimeMillis(),
                        report = freshReport
                    )
                )
            }
        }

        if (remoteResult is Result.Error && localResult is Result.Success) {
            val cachedReport = localResult.data.report
            if (cachedReport != null) {
                Result.Success(cachedReport)
            } else {
                remoteResult
            }
        } else {
            remoteResult
        }
    }

    override suspend fun getCachedForecast(location: String): Result<WeatherLocation> =
        withContext(dispatcherProvider.io) {
            localWeatherDataSource.fetchForecast(location)
        }

    override suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> =
        withContext(dispatcherProvider.io) {
            remoteWeatherDataSource.getSearchResult(location)
        }

    override fun getAllSavedLocations(): Flow<Result<List<WeatherLocation>>> {
        return localWeatherDataSource.fetchSavedLocations()
    }

    override suspend fun saveLocation(location: WeatherLocation): Result<Unit> =
        withContext(dispatcherProvider.io) {
            localWeatherDataSource.saveLocation(location)
        }

    override suspend fun deleteLocation(locationId: Long): Result<Unit> =
        withContext(dispatcherProvider.io) {
            localWeatherDataSource.deleteLocation(locationId)
        }

}
