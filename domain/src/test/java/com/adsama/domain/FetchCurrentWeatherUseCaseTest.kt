package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FetchCurrentWeatherUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var timeProvider: TimeProvider
    private lateinit var useCase: FetchCurrentWeatherUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        timeProvider = mockk()
        useCase = FetchCurrentWeatherUseCase(weatherDataSource, timeProvider)
    }

    @Test
    fun `execute returns error when location is blank`() = runTest {
        val result = useCase.invoke(FetchCurrentWeatherUseCase.Params("")).last()

        assertTrue(result is Result.Error<*>)
        val error = (result as Result.Error<*>).error
        assertTrue(error is DomainError.ValidationError)
        assertEquals("Location cannot be empty or blank", error.message)
    }

    @Test
    fun `execute returns cached data when fresh and not forced`() = runTest {
        val locationName = "London"
        val mockReport = mockk<WeatherReport>()
        val mockLocation = mockk<WeatherLocation> {
            every { lastUpdatedEpoch } returns 1000L
            every { report } returns mockReport
        }

        coEvery { weatherDataSource.getCachedForecast(locationName) } returns Result.Success(
            mockLocation
        )
        every { timeProvider.getCurrentTimeMillis() } returns 1000L + WeatherConstants.REFRESH_THRESHOLD_MS - 100

        val result = useCase.invoke(FetchCurrentWeatherUseCase.Params(locationName)).last()

        assertTrue(result is Result.Success<*>)
        assertEquals(mockReport, (result as Result.Success<WeatherReport>).data)
    }

    @Test
    fun `execute returns remote data when cache is stale`() = runTest {
        val locationName = "London"
        val mockReport = mockk<WeatherReport>()
        val mockLocation = mockk<WeatherLocation> {
            every { lastUpdatedEpoch } returns 1000L
            every { report } returns mockk()
        }

        coEvery { weatherDataSource.getCachedForecast(locationName) } returns Result.Success(
            mockLocation
        )
        every { timeProvider.getCurrentTimeMillis() } returns 1000L + WeatherConstants.REFRESH_THRESHOLD_MS + 100
        coEvery { weatherDataSource.getForecast(locationName) } returns Result.Success(mockReport)

        val result = useCase.invoke(FetchCurrentWeatherUseCase.Params(locationName)).last()

        assertTrue(result is Result.Success<*>)
        assertEquals(mockReport, (result as Result.Success<WeatherReport>).data)
    }

    @Test
    fun `execute returns remote data when forced even if fresh`() = runTest {
        val locationName = "London"
        val mockReport = mockk<WeatherReport>()
        val mockLocation = mockk<WeatherLocation> {
            every { lastUpdatedEpoch } returns 1000L
            every { report } returns mockk()
        }

        coEvery { weatherDataSource.getCachedForecast(locationName) } returns Result.Success(
            mockLocation
        )
        every { timeProvider.getCurrentTimeMillis() } returns 1000L + 100
        coEvery { weatherDataSource.getForecast(locationName) } returns Result.Success(mockReport)

        val result =
            useCase.invoke(FetchCurrentWeatherUseCase.Params(locationName, forceRefresh = true))
                .last()

        assertTrue(result is Result.Success<*>)
        assertEquals(mockReport, (result as Result.Success<WeatherReport>).data)
    }

}