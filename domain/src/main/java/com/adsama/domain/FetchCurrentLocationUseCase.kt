package com.adsama.domain

import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import javax.inject.Inject

class FetchCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<WeatherLocation> {
        return locationRepository.getCurrentLocation()
    }
}
