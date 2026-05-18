package com.adsama.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class Result<out T> {
    data class Loading<T>(val data: T? = null) : Result<T>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val error: DomainError, val data: T? = null) : Result<T>()
}

abstract class ResultFlowUseCase<in P, R> {
    operator fun invoke(parameters: P): Flow<Result<R>> = flow {
        try {
            emit(Result.Loading())
            when (val result = execute(parameters)) {
                is Result.Success -> emit(Result.Success(result.data))
                is Result.Error -> emit(Result.Error(result.error, result.data))
                is Result.Loading -> emit(Result.Loading(result.data))
            }
        } catch (e: Exception) {
            emit(Result.Error(DomainError.from(e)))
        }
    }

    protected abstract suspend fun execute(parameters: P): Result<R>
}
