package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import com.adsama.domain.model.WeatherLocation
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<WeatherLocation, Unit>() {

    override suspend fun execute(parameters: WeatherLocation): Result<Unit> {
        if (parameters.name.isBlank()) {
            return Result.Error(DomainError.ValidationError("Location name cannot be empty"))
        }
        return weatherDataSource.deleteLocation(parameters)
    }

}
