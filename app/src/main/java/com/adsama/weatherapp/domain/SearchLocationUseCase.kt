package com.adsama.weatherapp.domain

import com.adsama.model.SearchResponse
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<SearchLocationUseCase.RequestValues, SearchLocationUseCase.ResponseValue>() {

    public override suspend fun executeUseCase(requestValues: RequestValues?): ResponseValue {
        val responseValue = mWeatherDataSource.getSearchResult(requestValues!!.location)
        return ResponseValue(responseValue)
    }

    data class RequestValues(val location: String) : UseCase.RequestValues
    data class ResponseValue(val searchResponse: List<SearchResponse>) : UseCase.ResponseValue

}