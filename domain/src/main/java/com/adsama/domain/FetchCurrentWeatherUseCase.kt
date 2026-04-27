package com.adsama.domain

import com.adsama.model.AppError
import com.adsama.model.ForecastResponse
import com.adsama.model.Result
import com.adsama.model.ResultFlowUseCase
import javax.inject.Inject

class FetchCurrentWeatherUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<String, ForecastResponse>() {

    override suspend fun execute(parameters: String): Result<ForecastResponse> {
        if (parameters.isBlank()) {
            return Result.Error(AppError.ValidationError("Location cannot be empty or blank"))
        }
        return weatherDataSource.getForecast(parameters)
    }

}