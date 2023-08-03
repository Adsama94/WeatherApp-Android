package com.adsama.weatherapp.source.remote

import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse
import com.adsama.network.BuildConfig
import com.adsama.network.ResponseWrapper
import com.adsama.network.WeatherApi
import com.adsama.network.safeApiCall
import retrofit2.Response
import javax.inject.Inject

class RemoteWeatherSource @Inject constructor(weatherApi: WeatherApi) {

    private val apiKey = BuildConfig.API_KEY

    private val mWeatherApi = weatherApi

    suspend fun getSearchResult(location: String): ResponseWrapper<Response<List<SearchResponse>>> {
        return safeApiCall {
            mWeatherApi.weatherService.getSearchResultsAsync(apiKey, location).await()
        }
    }

    suspend fun getWeatherForecast(location: String): ResponseWrapper<Response<ForecastResponse>> {
        return safeApiCall {
            mWeatherApi.weatherService.getForecastAsync(apiKey, location).await()
        }
    }

}