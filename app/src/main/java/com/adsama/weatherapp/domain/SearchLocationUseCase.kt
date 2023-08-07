package com.adsama.weatherapp.domain

import com.adsama.model.SearchResponse
import com.adsama.weatherapp.source.WeatherDataSource
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(private val mWeatherDataSource: WeatherDataSource) :
    UseCase<SearchLocationUseCase.RequestValues, SearchLocationUseCase.ResponseValue>() {
    interface SearchLocationCallbacks : UseCaseCallback<ResponseValue>

    override suspend fun executeUseCase(requestValues: RequestValues?) {
        mWeatherDataSource.getSearchResult(
            requestValues!!.location,
            object : WeatherDataSource.LoadSearchCallback {
                override fun onSearchLoaded(searchResponse: List<SearchResponse>) {
                    val responseValue = ResponseValue(searchResponse)
                    (useCaseCallback as SearchLocationCallbacks).onSuccess(responseValue)
                }

                override fun onError(t: Throwable) {
                    (useCaseCallback as SearchLocationCallbacks).onError(t)
                }

            })
    }

    data class RequestValues(val location: String) : UseCase.RequestValues
    data class ResponseValue(val searchResponse: List<SearchResponse>) : UseCase.ResponseValue

}