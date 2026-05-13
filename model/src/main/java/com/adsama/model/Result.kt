package com.adsama.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Sealed Result class for type-safe handling of async operations.
 *
 * Represents three states:
 * - Loading: Operation in progress (optional data for cached/partial results)
 * - Success: Operation completed successfully with data
 * - Error: Operation failed with an exception (optional cached data)
 */
sealed class Result<out T> {
    data class Loading<T>(val data: T? = null) : Result<T>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception, val data: T? = null) : Result<T>()

    /**
     * Get error message with fallback
     */
    fun getErrorMessage(): String = when (this) {
        is Error -> exception.message ?: "Unknown error occurred"
        else -> ""
    }
}

/**
 * Abstract base class for use cases that work directly with Result<T>.
 *
 * Use this when your repository/source already returns Result<T>.
 * This avoids unnecessary exception catching since errors are already properly typed.
 *
 * @param P The parameter type for the use case
 * @param R The return data type
 *
 * Usage:
 * ```
 * class FetchWeatherUseCase(private val repo: WeatherRepo) : ResultFlowUseCase<String, ForecastResponse>() {
 *     override suspend fun execute(parameters: String): Result<ForecastResponse> {
 *         return repo.getForecast(parameters)  // Already returns Result<T>
 *     }
 * }
 * ```
 */
abstract class ResultFlowUseCase<in P, R> {

    operator fun invoke(parameters: P): Flow<Result<R>> = flow {
        try {
            emit(Result.Loading())
            when (val result = execute(parameters)) {
                is Result.Success -> emit(Result.Success(result.data))
                is Result.Error -> emit(Result.Error(result.exception, result.data))
                is Result.Loading -> {
                    // Nested loading, just pass through
                    emit(Result.Loading(result.data))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(AppError.from(e)))
        }
    }

    protected abstract suspend fun execute(parameters: P): Result<R>
}