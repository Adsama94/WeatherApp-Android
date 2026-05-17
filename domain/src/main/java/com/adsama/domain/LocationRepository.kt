package com.adsama.domain

import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<WeatherLocation>
}
