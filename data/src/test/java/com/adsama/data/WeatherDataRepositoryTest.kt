package com.adsama.data

import com.adsama.domain.DispatcherProvider
import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.RemoteWeatherDataSource
import com.adsama.domain.TimeProvider
import com.adsama.domain.model.CurrentWeather
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherDataRepositoryTest {

    private lateinit var localDataSource: LocalWeatherDataSource
    private lateinit var remoteDataSource: RemoteWeatherDataSource
    private lateinit var timeProvider: TimeProvider
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var repository: WeatherDataRepository
    private val locationName = "London"

    private val mockLocation = WeatherLocation(
        id = 1,
        name = "London",
        region = "Greater London",
        country = "United Kingdom",
        latitude = 51.5,
        longitude = -0.12,
        lastUpdatedEpoch = 1000L,
        report = null // Keep report null initially to simplify comparison
    )

    private fun createMockReport(location: WeatherLocation) = WeatherReport(
        location = location,
        current = CurrentWeather(
            tempC = 15.0,
            feelsLikeC = 14.0,
            conditionText = "Sunny",
            conditionIcon = "icon",
            windKph = 10.0,
            windDir = "N",
            precipMm = 0.0,
            uv = 5.0
        ),
        forecast = emptyList(),
        alerts = emptyList()
    )

    @Before
    fun setUp() {
        localDataSource = mockk()
        remoteDataSource = mockk()
        timeProvider = mockk()
        dispatcherProvider = mockk {
            val testDispatcher = UnconfinedTestDispatcher()
            every { io } returns testDispatcher
            every { main } returns testDispatcher
            every { default } returns testDispatcher
        }
        repository =
            WeatherDataRepository(
                localDataSource,
                remoteDataSource,
                timeProvider,
                dispatcherProvider
            )
    }

    @Test
    fun `getForecast always calls remote and updates local`() = runTest {
        val mockReport = createMockReport(mockLocation)
        val currentTime = 1000L

        every { timeProvider.getCurrentTimeMillis() } returns currentTime
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(mockLocation)
        coEvery { remoteDataSource.getWeatherForecast(locationName) } returns Result.Success(
            mockReport
        )
        coEvery { localDataSource.saveLocation(any()) } returns Result.Success(Unit)

        val result = repository.getForecast(locationName)

        assertTrue(result is Result.Success)
        val actualReport = (result as Result.Success).data
        assertEquals(mockReport.current, actualReport.current)
        coVerify(exactly = 1) { remoteDataSource.getWeatherForecast(locationName) }
        coVerify(exactly = 1) { localDataSource.saveLocation(any()) }
    }

    @Test
    fun `getForecast returns local fallback when remote fails`() = runTest {
        val mockReport = createMockReport(mockLocation)
        val locationWithReport = mockLocation.copy(report = mockReport)

        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(
            locationWithReport
        )
        coEvery { remoteDataSource.getWeatherForecast(locationName) } returns Result.Error(
            DomainError.NetworkError("No internet")
        )

        val result = repository.getForecast(locationName)

        assertTrue(result is Result.Success)
        assertEquals(mockReport, (result as Result.Success).data)
    }

    @Test
    fun `getCachedForecast returns local data`() = runTest {
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(mockLocation)

        val result = repository.getCachedForecast(locationName)

        assertTrue(result is Result.Success)
        assertEquals(mockLocation, (result as Result.Success).data)
    }

    @Test
    fun `getSearchResult propagates remote success`() = runTest {
        val locations = listOf(mockLocation)
        coEvery { remoteDataSource.getSearchResult(locationName) } returns Result.Success(locations)

        val result = repository.getSearchResult(locationName)

        assertTrue(result is Result.Success)
        assertEquals(locations, (result as Result.Success).data)
    }

    @Test
    fun `getAllSavedLocations propagates local flow`() = runTest {
        val flow = flowOf(Result.Success(listOf(mockLocation)))
        every { localDataSource.fetchSavedLocations() } returns flow

        val results = repository.getAllSavedLocations().toList()

        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Success)
    }

    @Test
    fun `saveLocation propagates local success`() = runTest {
        coEvery { localDataSource.saveLocation(mockLocation) } returns Result.Success(Unit)

        val result = repository.saveLocation(mockLocation)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `deleteLocation propagates local success`() = runTest {
        coEvery { localDataSource.deleteLocation(1L) } returns Result.Success(Unit)

        val result = repository.deleteLocation(1L)

        assertTrue(result is Result.Success)
    }

}