package com.adsama.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchSaveLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) {
    operator fun invoke(parameters: Unit): Flow<Result<List<PersistedWeatherModel>>> {
        return weatherDataSource.getAllSavedLocations()
    }
}