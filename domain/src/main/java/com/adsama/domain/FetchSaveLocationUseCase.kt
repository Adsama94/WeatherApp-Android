package com.adsama.domain

import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchSaveLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) {
    operator fun invoke(parameters: Unit): Flow<Result<List<WeatherLocation>>> {
        return weatherDataSource.getAllSavedLocations()
    }
}
