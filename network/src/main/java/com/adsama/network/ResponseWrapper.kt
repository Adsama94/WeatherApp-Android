package com.adsama.network

import com.adsama.model.WeatherErrorResponse

sealed class ResponseWrapper<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResponseWrapper<T>()
    data class NetworkError(val error: WeatherErrorResponse) : ResponseWrapper<Nothing>()
    object Failure : ResponseWrapper<Nothing>()
}