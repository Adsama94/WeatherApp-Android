package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import com.adsama.domain.model.WeatherReport
import javax.inject.Inject

class FetchCurrentWeatherUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<FetchCurrentWeatherUseCase.Params, WeatherReport>() {

    data class Params(val location: String, val forceRefresh: Boolean = false)

    override suspend fun execute(parameters: Params): Result<WeatherReport> {
        if (parameters.location.isBlank()) {
            return Result.Error(DomainError.ValidationError("Location cannot be empty or blank"))
        }
        return weatherDataSource.getForecast(parameters.location, parameters.forceRefresh)
    }

}
