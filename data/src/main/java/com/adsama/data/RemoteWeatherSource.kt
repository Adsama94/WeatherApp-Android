package com.adsama.data

import com.adsama.model.AppError
import com.adsama.model.ForecastResponse
import com.adsama.model.Result
import com.adsama.model.SearchResponse
import com.adsama.network.BuildConfig
import com.adsama.network.ResponseWrapper
import com.adsama.network.WeatherApi
import com.adsama.network.safeApiCall
import javax.inject.Inject

class RemoteWeatherSource @Inject constructor(private val weatherApi: WeatherApi) {

    private val apiKey = BuildConfig.API_KEY

    suspend fun getSearchResult(location: String): Result<List<SearchResponse>> {
        return try {
            when (val response = safeApiCall {
                weatherApi.weatherService.getSearchResultsAsync(apiKey, location).await()
            }) {
                is ResponseWrapper.Success -> {
                    response.data.body()?.let { Result.Success(it) }
                        ?: Result.Error(AppError.EmptyResponseError("Empty response body"))
                }
                is ResponseWrapper.Failure ->
                    Result.Error(AppError.NetworkError("Network request failed"))
                is ResponseWrapper.NetworkError ->
                    Result.Error(AppError.ApiError(
                        response.error.error.code ?: 0,
                        response.error.error.message ?: "Unknown error"
                    ))
            }
        } catch (e: Exception) {
            Result.Error(AppError.from(e))
        }
    }

    suspend fun getWeatherForecast(location: String): Result<ForecastResponse> {
        return try {
            when (val response = safeApiCall {
                weatherApi.weatherService.getForecastAsync(apiKey, location).await()
            }) {
                is ResponseWrapper.Success -> {
                    response.data.body()?.let { Result.Success(it) }
                        ?: Result.Error(AppError.EmptyResponseError("Empty response body"))
                }
                is ResponseWrapper.Failure ->
                    Result.Error(AppError.NetworkError("Network request failed"))
                is ResponseWrapper.NetworkError ->
                    Result.Error(AppError.ApiError(
                        response.error.error.code ?: 0,
                        response.error.error.message ?: "Unknown error"
                    ))
            }
        } catch (e: Exception) {
            Result.Error(AppError.from(e))
        }
    }

}