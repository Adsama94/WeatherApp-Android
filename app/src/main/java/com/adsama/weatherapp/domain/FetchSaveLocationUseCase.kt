package com.adsama.weatherapp.domain

import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class FetchSaveLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<FetchSaveLocationUseCase.RequestValues, FetchSaveLocationUseCase.ResponseValue>() {

    interface FetchSaveLocationCallback : UseCaseCallback<ResponseValue>

    override suspend fun executeUseCase(requestValues: RequestValues?) {
        mWeatherDataSource.getAllSavedLocations(object : WeatherDataSource.LoadSavedCallback {
            override fun onSavedLoaded(savedList: List<PersistedWeatherModel>) {
                val responseValue = ResponseValue(savedList)
                (useCaseCallback as FetchSaveLocationCallback).onSuccess(responseValue)
            }

            override fun onError(t: Throwable) {
                (useCaseCallback as FetchSaveLocationCallback).onError(t)
            }

        })
    }

    data class RequestValues(val requestValue: String? = null) :
        UseCase.RequestValues

    data class ResponseValue(val storedLocations: List<PersistedWeatherModel>) :
        UseCase.ResponseValue

}