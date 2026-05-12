package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import com.adsama.domain.model.WeatherReport
import javax.inject.Inject

class FetchCurrentWeatherUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<String, WeatherReport>() {

    override suspend fun execute(parameters: String): Result<WeatherReport> {
        if (parameters.isBlank()) {
            return Result.Error(DomainError.ValidationError("Location cannot be empty or blank"))
        }
        return weatherDataSource.getForecast(parameters)
    }

}
