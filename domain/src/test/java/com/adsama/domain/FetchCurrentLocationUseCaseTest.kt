package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class FetchCurrentLocationUseCaseTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: FetchCurrentLocationUseCase

    @Before
    fun setUp() {
        locationRepository = mockk()
        useCase = FetchCurrentLocationUseCase(locationRepository)
    }

    @Test
    fun `when repository returns success, use case should return success with same data`() =
        runTest {
            val mockLocation = WeatherLocation(
                id = 1, name = "Test City", latitude = 0.0, longitude = 0.0,
                region = "Random Region",
                country = "Random Country",
                temperature = 23.3,
                conditionText = null,
                conditionIcon = null,
                lastUpdated = null,
                lastUpdatedEpoch = 0L,
                report = null
            )
            coEvery { locationRepository.getCurrentLocation() } returns Result.Success(mockLocation)

            val result = useCase()

            assertTrue(result is Result.Success)
            assertEquals(mockLocation, (result as Result.Success).data)
            coVerify(exactly = 1) { locationRepository.getCurrentLocation() }
        }

    @Test
    fun `when repository return error, use case should propagate the error`() = runTest {
        val errorWithMessage = DomainError.UnknownError("Location Service is unavailable")
        coEvery { locationRepository.getCurrentLocation() } returns Result.Error<WeatherLocation>(errorWithMessage)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(errorWithMessage, (result as Result.Error).error)
    }

}