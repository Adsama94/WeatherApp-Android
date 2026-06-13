package com.adsama.network

import com.adsama.domain.DispatcherProvider
import com.adsama.domain.RemoteWeatherDataSource
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.network.adapter.NetworkResponse
import com.adsama.network.mapper.WeatherRemoteMapper
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteWeatherDataSourceImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val mapper: WeatherRemoteMapper,
    private val dispatcherProvider: DispatcherProvider
) : RemoteWeatherDataSource {

    override suspend fun getSearchResult(location: String): Result<List<WeatherLocation>> =
        withContext(dispatcherProvider.io) {
            when (val response = weatherService.getSearchResults(location)) {
                is NetworkResponse.Success -> {
                    Result.Success(response.body.map { mapper.mapSearchResponseToDomain(it) })
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
                    Result.Error(
                        DomainError.UnknownError(
                            "Unknown error occurred: ${response.error?.message ?: "No message"}"
                        )
                    )
                }
            }
        }

    override suspend fun getWeatherForecast(location: String): Result<WeatherReport> =
        withContext(dispatcherProvider.io) {
            when (val response = weatherService.getForecast(location)) {
                is NetworkResponse.Success -> {
                    Result.Success(mapper.mapForecastResponseToDomain(response.body))
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
                    Result.Error(
                        DomainError.UnknownError(
                            "Unknown error occurred: ${response.error?.message ?: "No message"}"
                        )
                    )
                }
            }
        }
}
