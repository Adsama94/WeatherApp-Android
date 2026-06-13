package com.adsama.domain

import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<Long, Unit>() {

    override suspend fun execute(parameters: Long): Result<Unit> {
        return weatherDataSource.deleteLocation(parameters)
    }

}
