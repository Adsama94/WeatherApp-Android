package com.adsama.domain

import com.adsama.model.AppError
import com.adsama.model.Result
import com.adsama.model.ResultFlowUseCase
import com.adsama.model.SearchResponse
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : ResultFlowUseCase<String, List<SearchResponse>>() {

    override suspend fun execute(parameters: String): Result<List<SearchResponse>> {
        if (parameters.isBlank()) {
            return Result.Error(AppError.ValidationError("Search query cannot be empty"))
        }
        return weatherDataSource.getSearchResult(parameters)
    }

}