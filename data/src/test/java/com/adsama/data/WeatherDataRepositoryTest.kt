package com.adsama.data

import com.adsama.domain.LocalWeatherDataSource
import com.adsama.domain.RemoteWeatherDataSource
import com.adsama.domain.TimeProvider
import com.adsama.domain.WeatherConstants
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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherDataRepositoryTest {

    private lateinit var localDataSource: LocalWeatherDataSource
    private lateinit var remoteDataSource: RemoteWeatherDataSource
    private lateinit var timeProvider: TimeProvider
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
        repository = WeatherDataRepository(localDataSource, remoteDataSource, timeProvider)
    }

    @Test
    fun `getForecast returns local data when fresh and not forced`() = runTest {
        // Given
        val mockReport = createMockReport(mockLocation)
        val locationWithReport = mockLocation.copy(report = mockReport)
        val currentTime = 1000L + WeatherConstants.REFRESH_THRESHOLD_MS - 100
        
        every { timeProvider.getCurrentTimeMillis() } returns currentTime
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(locationWithReport)

        // When
        val result = repository.getForecast(locationName, forceRefresh = false)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockReport, (result as Result.Success).data)
        coVerify(exactly = 0) { remoteDataSource.getWeatherForecast(any()) }
    }

    @Test
    fun `getForecast calls remote when stale`() = runTest {
        // Given
        val currentTime = 1000L + WeatherConstants.REFRESH_THRESHOLD_MS + 100
        val mockReport = createMockReport(mockLocation)
        
        every { timeProvider.getCurrentTimeMillis() } returns currentTime
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(mockLocation)
        coEvery { remoteDataSource.getWeatherForecast(locationName) } returns Result.Success(mockReport)
        coEvery { localDataSource.saveLocation(any()) } returns Result.Success(Unit)

        // When
        val result = repository.getForecast(locationName, forceRefresh = false)

        // Then
        assertTrue(result is Result.Success)
        // We compare specific fields instead of whole object to avoid circular ref issues in toString/equals
        val actualReport = (result as Result.Success).data
        assertEquals(mockReport.current, actualReport.current)
        coVerify(exactly = 1) { remoteDataSource.getWeatherForecast(locationName) }
        coVerify(exactly = 1) { localDataSource.saveLocation(any()) }
    }

    @Test
    fun `getForecast calls remote when forced even if fresh`() = runTest {
        // Given
        val currentTime = 1000L + 100
        val mockReport = createMockReport(mockLocation)

        every { timeProvider.getCurrentTimeMillis() } returns currentTime
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(mockLocation)
        coEvery { remoteDataSource.getWeatherForecast(locationName) } returns Result.Success(mockReport)
        coEvery { localDataSource.saveLocation(any()) } returns Result.Success(Unit)

        // When
        val result = repository.getForecast(locationName, forceRefresh = true)

        // Then
        assertTrue(result is Result.Success)
        val actualReport = (result as Result.Success).data
        assertEquals(mockReport.current, actualReport.current)
        coVerify(exactly = 1) { remoteDataSource.getWeatherForecast(locationName) }
    }

    @Test
    fun `getForecast returns local fallback when remote fails`() = runTest {
        // Given
        val mockReport = createMockReport(mockLocation)
        val locationWithReport = mockLocation.copy(report = mockReport)
        val currentTime = 1000L + WeatherConstants.REFRESH_THRESHOLD_MS + 100
        
        every { timeProvider.getCurrentTimeMillis() } returns currentTime
        coEvery { localDataSource.fetchForecast(locationName) } returns Result.Success(locationWithReport)
        coEvery { remoteDataSource.getWeatherForecast(locationName) } returns Result.Error(DomainError.NetworkError("No internet"))

        // When
        val result = repository.getForecast(locationName, forceRefresh = false)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockReport, (result as Result.Success).data)
    }

    @Test
    fun `getSearchResult propagates remote success`() = runTest {
        // Given
        val locations = listOf(mockLocation)
        coEvery { remoteDataSource.getSearchResult(locationName) } returns Result.Success(locations)

        // When
        val result = repository.getSearchResult(locationName)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(locations, (result as Result.Success).data)
    }

    @Test
    fun `getAllSavedLocations propagates local flow`() = runTest {
        // Given
        val flow = flowOf(Result.Success(listOf(mockLocation)))
        every { localDataSource.fetchSavedLocations() } returns flow

        // When
        val results = repository.getAllSavedLocations().toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Success)
    }

    @Test
    fun `saveLocation propagates local success`() = runTest {
        // Given
        coEvery { localDataSource.saveLocation(mockLocation) } returns Result.Success(Unit)

        // When
        val result = repository.saveLocation(mockLocation)

        // Then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `deleteLocation propagates local success`() = runTest {
        // Given
        coEvery { localDataSource.deleteLocation(mockLocation) } returns Result.Success(Unit)

        // When
        val result = repository.deleteLocation(mockLocation)

        // Then
        assertTrue(result is Result.Success)
    }
}
