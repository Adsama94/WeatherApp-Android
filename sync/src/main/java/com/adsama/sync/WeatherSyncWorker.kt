package com.adsama.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adsama.domain.DispatcherProvider
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class WeatherSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherDataSource: WeatherDataSource,
    private val dispatcherProvider: DispatcherProvider
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io) {
            try {
                val savedLocationsResult = weatherDataSource.getAllSavedLocations()
                    .first { it !is Result.Loading }
                
                if (savedLocationsResult is com.adsama.domain.model.Result.Success) {
                    val locations = savedLocationsResult.data
                    
                    // Refresh each location
                    locations.forEach { location ->
                        // getForecast with forceRefresh = true will update the local DB
                        weatherDataSource.getForecast(location.name, forceRefresh = true)
                    }
                    
                    Result.success()
                } else {
                    // If we couldn't even read the local DB, we might want to retry
                    Result.retry()
                }
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}
