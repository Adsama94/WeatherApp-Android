package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import com.adsama.domain.model.WeatherReport
import javax.inject.Inject

class FetchCurrentWeatherUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
    private val timeProvider: TimeProvider
) : ResultFlowUseCase<FetchCurrentWeatherUseCase.Params, WeatherReport>() {

    data class Params(val location: String, val forceRefresh: Boolean = false)

    override suspend fun execute(parameters: Params): Result<WeatherReport> {
        if (parameters.location.isBlank()) {
            return Result.Error(DomainError.ValidationError("Location cannot be empty or blank"))
        }

        val cachedResult = weatherDataSource.getCachedForecast(parameters.location)
        val currentTime = timeProvider.getCurrentTimeMillis()

        if (cachedResult is Result.Success) {
            val cachedLocation = cachedResult.data
            val isFresh =
                (currentTime - cachedLocation.lastUpdatedEpoch < WeatherConstants.REFRESH_THRESHOLD_MS)

            if (!parameters.forceRefresh && isFresh && cachedLocation.report != null) {
                return Result.Success(cachedLocation.report!!)
            }
        }

        return weatherDataSource.getForecast(parameters.location)
    }

}
