package com.adsama.weatherapp.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<DeleteLocationUseCase.RequestValues, DeleteLocationUseCase.ResponseValue>() {

    override suspend fun executeUseCase(requestValues: RequestValues?) {
        mWeatherDataSource.deleteLocation(requestValues!!.persistedWeatherModel)
    }

    data class RequestValues(val persistedWeatherModel: PersistedWeatherModel) :
        UseCase.RequestValues

    data class ResponseValue(val nothing: Nothing) : UseCase.ResponseValue

}