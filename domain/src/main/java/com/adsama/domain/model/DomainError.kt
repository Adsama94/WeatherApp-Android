package com.adsama.domain.model

sealed class DomainError(override val message: String) : Exception(message) {
    data class NetworkError(val msg: String) : DomainError(msg)
    data class ApiError(val code: Int, val msg: String) : DomainError(msg)
    data class DatabaseError(val msg: String) : DomainError(msg)
    data class ValidationError(val msg: String) : DomainError(msg)
    data class UnknownError(val msg: String) : DomainError(msg)

    companion object {
        fun from(throwable: Throwable): DomainError {
            return when (throwable) {
                is DomainError -> throwable
                else -> UnknownError(throwable.message ?: "An unknown error occurred")
            }
        }
    }
}
