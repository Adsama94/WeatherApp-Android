package com.adsama.domain

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.adsama.domain.model.WeatherLocation
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchSavedLocationsPagingUseCase @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) {
    operator fun invoke(pagingConfig: PagingConfig): Flow<PagingData<WeatherLocation>> {
        return weatherDataSource.getPagingSavedLocations(pagingConfig)
    }
}
