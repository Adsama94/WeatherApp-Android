package com.adsama.weatherapp.domain

import com.adsama.model.ForecastResponse
import com.adsama.weatherapp.source.WeatherDataSource

class FetchCurrentWeatherUseCase(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<FetchCurrentWeatherUseCase.RequestValues, FetchCurrentWeatherUseCase.ResponseValue>() {

    public override suspend fun executeUseCase(requestValues: RequestValues?): ResponseValue {
        val forecastResponse = mWeatherDataSource.getForecast(requestValues!!.location)
        return ResponseValue(forecastResponse)
    }

    data class RequestValues(val location: String) : UseCase.RequestValues
    data class ResponseValue(val forecastResponse: ForecastResponse) : UseCase.ResponseValue

}