package com.adsama.network

import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse
import com.adsama.model.WeatherErrorResponse
import com.adsama.network.adapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast.json?days=5&alerts=yes")
    suspend fun getForecast(
        @Query("q") location: String
    ): NetworkResponse<ForecastResponse, WeatherErrorResponse>

    @GET("search.json")
    suspend fun getSearchResults(
        @Query("q") location: String
    ): NetworkResponse<List<SearchResponse>, WeatherErrorResponse>

}