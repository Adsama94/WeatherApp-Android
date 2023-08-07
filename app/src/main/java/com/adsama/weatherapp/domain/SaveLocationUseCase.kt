package com.adsama.weatherapp.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<SaveLocationUseCase.RequestValues, SaveLocationUseCase.ResponseValue>() {

    override suspend fun executeUseCase(requestValues: RequestValues?) {
        mWeatherDataSource.saveLocation(requestValues!!.persistedWeatherModel)
    }

    data class RequestValues(val persistedWeatherModel: PersistedWeatherModel) :
        UseCase.RequestValues

    data class ResponseValue(val insertionResponse: String) : UseCase.ResponseValue

}