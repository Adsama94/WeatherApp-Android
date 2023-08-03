package com.adsama.weatherapp.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class FetchSaveLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<FetchSaveLocationUseCase.RequestValues, FetchSaveLocationUseCase.ResponseValue>() {

    public override suspend fun executeUseCase(requestValues: RequestValues?): ResponseValue {
        val savedLocationsResponse = mWeatherDataSource.getAllSavedLocations()
        return ResponseValue(savedLocationsResponse)
    }

    data class RequestValues(val requestValue: String? = null) :
        UseCase.RequestValues

    data class ResponseValue(val storedLocations: List<PersistedWeatherModel>) :
        UseCase.ResponseValue

}