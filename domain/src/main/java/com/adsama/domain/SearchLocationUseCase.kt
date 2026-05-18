package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.ResultFlowUseCase
import com.adsama.domain.model.WeatherLocation
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<String, List<WeatherLocation>>() {

    override suspend fun execute(parameters: String): Result<List<WeatherLocation>> {
        if (parameters.isBlank()) {
            return Result.Error(DomainError.ValidationError("Search query cannot be empty"))
        }
        return weatherDataSource.getSearchResult(parameters)
    }

}
