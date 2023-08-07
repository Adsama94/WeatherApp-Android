package com.adsama.weatherapp.domain

import com.adsama.model.ForecastResponse
import com.adsama.weatherapp.source.WeatherDataSource

class FetchCurrentWeatherUseCase(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<FetchCurrentWeatherUseCase.RequestValues, FetchCurrentWeatherUseCase.ResponseValue>() {

    interface FetchCurrentWeatherCallbacks : UseCaseCallback<ResponseValue>

    override suspend fun executeUseCase(requestValues: RequestValues?) {
        mWeatherDataSource.getForecast(
            requestValues!!.location,
            object : WeatherDataSource.LoadForecastCallback {
                override fun onForecastLoaded(forecastResponse: ForecastResponse) {
                    val responseValue = ResponseValue(forecastResponse)
                    (useCaseCallback as FetchCurrentWeatherCallbacks).onSuccess(responseValue)
                }

                override fun onError(t: Throwable) {
                    (useCaseCallback as FetchCurrentWeatherCallbacks).onError(t)
                }

            })
    }

    data class RequestValues(val location: String) : UseCase.RequestValues
    data class ResponseValue(val forecastResponse: ForecastResponse) : UseCase.ResponseValue

}