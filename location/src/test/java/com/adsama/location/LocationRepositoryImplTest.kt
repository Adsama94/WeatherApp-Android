package com.adsama.location

import android.location.Location
import com.adsama.domain.model.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationRepositoryImplTest {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        fusedLocationClient = mockk()
        repository = LocationRepositoryImpl(fusedLocationClient)
    }

    @Test
    fun `getCurrentLocation returns success when location is found`() = runTest {
        // Given
        val mockLocation = mockk<Location> {
            every { latitude } returns 51.5074
            every { longitude } returns -0.1278
        }
        val mockTask = mockk<Task<Location>>()
        
        every { fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) } returns mockTask
        
        // Mock the success listener callback
        every { mockTask.addOnSuccessListener(any<OnSuccessListener<Location>>()) } answers {
            val listener = it.invocation.args[0] as OnSuccessListener<Location>
            listener.onSuccess(mockLocation)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        // When
        val result = repository.getCurrentLocation()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(51.5074, (result as Result.Success).data.latitude, 0.0001)
        assertEquals(-0.1278, result.data.longitude, 0.0001)
    }

    @Test
    fun `getCurrentLocation returns error when location is null`() = runTest {
        // Given
        val mockTask = mockk<Task<Location>>()
        
        every { fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) } returns mockTask
        
        every { mockTask.addOnSuccessListener(any<OnSuccessListener<Location>>()) } answers {
            val listener = it.invocation.args[0] as OnSuccessListener<Location>
            listener.onSuccess(null)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        // When
        val result = repository.getCurrentLocation()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Unable to get current location", (result as Result.Error).error.message)
    }

    @Test
    fun `getCurrentLocation returns error when task fails`() = runTest {
        // Given
        val mockTask = mockk<Task<Location>>()
        val exception = Exception("GPS error")
        
        every { fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) } returns mockTask
        
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any<OnFailureListener>()) } answers {
            val listener = it.invocation.args[0] as OnFailureListener
            listener.onFailure(exception)
            mockTask
        }

        // When
        val result = repository.getCurrentLocation()

        // Then
        assertTrue(result is Result.Error)
        assertEquals("GPS error", (result as Result.Error).error.message)
    }
}
