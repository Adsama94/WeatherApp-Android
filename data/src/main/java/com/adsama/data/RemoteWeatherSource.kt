package com.adsama.data

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.network.BuildConfig
import com.adsama.network.ResponseWrapper
import com.adsama.network.WeatherApi
import com.adsama.network.safeApiCall
import javax.inject.Inject

class RemoteWeatherSource @Inject constructor(private val weatherApi: WeatherApi) {

    private val apiKey = BuildConfig.API_KEY

    suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> {
        return try {
            when (val response = safeApiCall {
                weatherApi.weatherService.getSearchResultsAsync(apiKey, location).await()
            }) {
                is ResponseWrapper.Success -> {
                    response.data.body()?.let {
                        Result.Success(it.map { searchResponse -> searchResponse.toDomain() })
                    } ?: Result.Error(DomainError.UnknownError("Empty response body"))
                }
                is ResponseWrapper.Failure ->
                    Result.Error(DomainError.NetworkError("Network request failed"))
                is ResponseWrapper.NetworkError ->
                    Result.Error(
                        DomainError.ApiError(
                        response.error.error.code ?: 0,
                        response.error.error.message ?: "Unknown error"
                    ))
            }
        } catch (e: Exception) {
            Result.Error(DomainError.from(e))
        }
    }

    suspend fun getWeatherForecast(location: String): Result<WeatherReport> {
        return try {
            when (val response = safeApiCall {
                weatherApi.weatherService.getForecastAsync(apiKey, location).await()
            }) {
                is ResponseWrapper.Success -> {
                    response.data.body()?.let {
                        Result.Success(it.toDomain())
                    } ?: Result.Error(DomainError.UnknownError("Empty response body"))
                }
                is ResponseWrapper.Failure ->
                    Result.Error(DomainError.NetworkError("Network request failed"))
                is ResponseWrapper.NetworkError ->
                    Result.Error(
                        DomainError.ApiError(
                        response.error.error.code ?: 0,
                        response.error.error.message ?: "Unknown error"
                    ))
            }
        } catch (e: Exception) {
            Result.Error(DomainError.from(e))
        }
    }

}
