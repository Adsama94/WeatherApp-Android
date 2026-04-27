package com.adsama.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.Result
import com.adsama.model.ResultFlowUseCase
import javax.inject.Inject

class FetchSaveLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<Unit, List<PersistedWeatherModel>>() {

    override suspend fun execute(parameters: Unit): Result<List<PersistedWeatherModel>> {
        return weatherDataSource.getAllSavedLocations()
    }

}