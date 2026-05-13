package com.adsama.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherErrorResponse(val error: Error)

@Serializable
data class Error(val code: Int, val message: String)