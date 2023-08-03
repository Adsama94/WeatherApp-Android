package com.adsama.network

import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast.json?days=5&alerts=yes")
    fun getForecastAsync(
        @Query("key") key: String,
        @Query("q") location: String
    ): Deferred<Response<ForecastResponse>>

    @GET("search.json?")
    fun getSearchResultsAsync(
        @Query("key") key: String,
        @Query("q") location: String
    ): Deferred<Response<List<SearchResponse>>>

}