package com.adsama.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherErrorResponse(
    @SerialName("error")
    val error: Error
)

@Serializable
data class Error(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String
)