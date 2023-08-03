package com.adsama.model

data class WeatherErrorResponse(
    val error: Error
)

data class Error(
    val code: Int? = 0,
    val message: String? = "Error"
)