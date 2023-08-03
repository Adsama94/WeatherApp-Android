package com.adsama.weatherapp.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<DeleteLocationUseCase.RequestValues, DeleteLocationUseCase.ResponseValue>() {

    public override suspend fun executeUseCase(requestValues: RequestValues?): ResponseValue {
        val deleteResponse =
            mWeatherDataSource.deleteLocation(requestValues!!.persistedWeatherModel)
        return ResponseValue(deleteResponse)
    }

    data class RequestValues(val persistedWeatherModel: PersistedWeatherModel) :
        UseCase.RequestValues

    data class ResponseValue(val deletionResponse: String) : UseCase.ResponseValue

}