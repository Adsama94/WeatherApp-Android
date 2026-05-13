package com.adsama.data

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.network.WeatherService
import com.adsama.network.adapter.NetworkResponse
import javax.inject.Inject

class RemoteWeatherSource @Inject constructor(private val weatherService: WeatherService) {

    suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> {
        return when (val response = weatherService.getSearchResults(location)) {
            is NetworkResponse.Success -> {
                Result.Success(response.body.map { it.toDomain() })
            }

            is NetworkResponse.ApiError -> {
                Result.Error(
                    DomainError.ApiError(
                        response.body.error.code,
                        response.body.error.message
                    )
                )
            }

            is NetworkResponse.NetworkError -> {
                Result.Error(DomainError.NetworkError("Network connection error: ${response.error.message}"))
            }

            is NetworkResponse.UnknownError -> {
                Result.Error(DomainError.UnknownError("Unknown error occurred: ${response.error?.message ?: "No message"}"))
            }
        }
    }

    suspend fun getWeatherForecast(location: String): Result<WeatherReport> {
        return when (val response = weatherService.getForecast(location)) {
            is NetworkResponse.Success -> {
                Result.Success(response.body.toDomain())
            }

            is NetworkResponse.ApiError -> {
                Result.Error(
                    DomainError.ApiError(
                        response.body.error.code,
                        response.body.error.message
                    )
                )
            }

            is NetworkResponse.NetworkError -> {
                Result.Error(DomainError.NetworkError("Network connection error: ${response.error.message}"))
            }

            is NetworkResponse.UnknownError -> {
                Result.Error(DomainError.UnknownError("Unknown error occurred: ${response.error?.message ?: "No message"}"))
            }
        }
    }

}
