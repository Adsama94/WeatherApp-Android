package com.adsama.model

/**
 * Sealed class representing different types of errors that can occur in the application.
 *
 * This enables type-safe error handling without throwing exceptions.
 * Errors are propagated through Result<T> objects in the Flow.
 */
sealed class AppError(override val message: String) : Exception(message) {

    /**
     * Network-related errors
     */
    data class NetworkError(val msg: String) : AppError(msg)

    /**
     * API response errors (4xx, 5xx)
     */
    data class ApiError(
        val code: Int,
        val msg: String
    ) : AppError(msg)

    /**
     * Empty response body from API
     */
    data class EmptyResponseError(val msg: String) : AppError(msg)

    /**
     * Database operation errors
     */
    data class DatabaseError(val msg: String) : AppError(msg)

    /**
     * Input validation errors
     */
    data class ValidationError(val msg: String) : AppError(msg)

    /**
     * Unknown/unexpected errors
     */
    data class UnknownError(val msg: String) : AppError(msg)

    companion object {
        /**
         * Convert a Throwable to an AppError
         */
        fun from(throwable: Throwable): AppError {
            return when (throwable) {
                is AppError -> throwable
                else -> UnknownError(throwable.message ?: "An unknown error occurred")
            }
        }
    }
}