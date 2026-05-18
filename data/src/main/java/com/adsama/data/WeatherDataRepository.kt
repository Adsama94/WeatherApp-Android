package com.adsama.data

import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.RemoteWeatherDataSource
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherDataRepository @Inject constructor(
    private val localWeatherDataSource: LocalWeatherDataSource,
    private val remoteWeatherDataSource: RemoteWeatherDataSource
) : WeatherDataSource {

    override suspend fun getForecast(location: String, forceRefresh: Boolean): Result<WeatherReport> {
        val localResult = localWeatherDataSource.fetchForecast(location)
        
        // If we have a local result with a report AND we're not forcing a refresh, return it
        if (!forceRefresh && localResult is Result.Success && localResult.data.report != null) {
            return Result.Success(localResult.data.report!!)
        }

        // Fetch from remote
        return remoteWeatherDataSource.getWeatherForecast(location).also { remoteResult ->
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
                            report = freshReport
                        )
                    )
                }
            }
        }.let { remoteResult ->
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
    }

    override suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> {
        return remoteWeatherDataSource.getSearchResult(location)
    }

    override fun getAllSavedLocations(): Flow<Result<List<WeatherLocation>>> {
        return localWeatherDataSource.fetchSavedLocations()
    }

    override suspend fun saveLocation(location: WeatherLocation): Result<Unit> {
        return localWeatherDataSource.saveLocation(location)
    }

    override suspend fun deleteLocation(location: WeatherLocation): Result<Unit> {
        return localWeatherDataSource.deleteLocation(location)
    }

}
