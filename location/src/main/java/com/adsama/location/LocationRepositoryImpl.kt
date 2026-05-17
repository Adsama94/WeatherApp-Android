package com.adsama.location

import android.annotation.SuppressLint
import android.content.Context
import com.adsama.domain.LocationRepository
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<WeatherLocation> =
        suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(
                            Result.Success(
                                WeatherLocation(
                                    name = "Current Location",
                                    region = "",
                                    country = "",
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        )
                    } else {
                        continuation.resume(Result.Error(DomainError.UnknownError("Unable to get current location")))
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(
                        Result.Error(
                            DomainError.UnknownError(
                                e.message ?: "Location error"
                            )
                        )
                    )
                }
        }
}
