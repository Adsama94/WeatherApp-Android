package com.adsama.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.AppError
import com.adsama.model.Result
import com.adsama.model.ResultFlowUseCase
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<PersistedWeatherModel, Unit>() {

    override suspend fun execute(parameters: PersistedWeatherModel): Result<Unit> {
        if (parameters.name.isBlank()) {
            return Result.Error(AppError.ValidationError("Location name cannot be empty"))
        }
        return weatherDataSource.saveLocation(parameters)
    }

}