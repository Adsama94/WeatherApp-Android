package com.adsama.network

import com.adsama.domain.DispatcherProvider
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.model.Error
import com.adsama.model.ForecastResponse
import com.adsama.model.SearchResponse
import com.adsama.model.WeatherErrorResponse
import com.adsama.network.adapter.NetworkResponse
import com.adsama.network.mapper.WeatherRemoteMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class RemoteWeatherDataSourceImplTest {

    private lateinit var weatherService: WeatherService
    private lateinit var mapper: WeatherRemoteMapper
    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var dataSource: RemoteWeatherDataSourceImpl

    @Before
    fun setUp() {
        weatherService = mockk()
        mapper = mockk()
        dispatcherProvider = mockk {
            val testDispatcher = UnconfinedTestDispatcher()
            every { io } returns testDispatcher
            every { main } returns testDispatcher
            every { default } returns testDispatcher
        }
        dataSource = RemoteWeatherDataSourceImpl(weatherService, mapper, dispatcherProvider)
    }

    @Test
    fun `getSearchResult returns success when service returns success`() = runTest {
        val location = "London"
        val searchResponse = listOf(mockk<SearchResponse>())
        val domainLocation = mockk<com.adsama.domain.model.WeatherLocation>()

        coEvery { weatherService.getSearchResults(location) } returns NetworkResponse.Success(
            searchResponse
        )
        every { mapper.mapSearchResponseToDomain(any()) } returns domainLocation

        val result = dataSource.getSearchResult(location)

        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
        assertEquals(domainLocation, result.data[0])
    }

    @Test
    fun `getSearchResult returns ApiError when service returns ApiError`() = runTest {
        val location = "London"
        val apiError = WeatherErrorResponse(Error(1001, "API Key invalid"))

        coEvery { weatherService.getSearchResults(location) } returns NetworkResponse.ApiError(
            apiError,
            401
        )

        val result = dataSource.getSearchResult(location)

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.ApiError)
        assertEquals(1001, (error as DomainError.ApiError).code)
        assertEquals("API Key invalid", error.message)
    }

    @Test
    fun `getSearchResult returns NetworkError when service returns NetworkError`() = runTest {
        val location = "London"
        val ioException = IOException("No internet")

        coEvery { weatherService.getSearchResults(location) } returns NetworkResponse.NetworkError(
            ioException
        )

        val result = dataSource.getSearchResult(location)

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.NetworkError)
        assertTrue(error.message.contains("No internet"))
    }

    @Test
    fun `getSearchResult returns UnknownError when service returns UnknownError`() = runTest {
        val location = "London"
        val throwable = Throwable("Unknown")

        coEvery { weatherService.getSearchResults(location) } returns NetworkResponse.UnknownError(
            throwable
        )

        val result = dataSource.getSearchResult(location)

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.UnknownError)
        assertTrue(error.message.contains("Unknown"))
    }

    @Test
    fun `getWeatherForecast returns success when service returns success`() = runTest {
        val location = "London"
        val forecastResponse = mockk<ForecastResponse>()
        val domainReport = mockk<com.adsama.domain.model.WeatherReport>()

        coEvery { weatherService.getForecast(location) } returns NetworkResponse.Success(
            forecastResponse
        )
        every { mapper.mapForecastResponseToDomain(forecastResponse) } returns domainReport

        val result = dataSource.getWeatherForecast(location)

        assertTrue(result is Result.Success)
        assertEquals(domainReport, (result as Result.Success).data)
    }

    @Test
    fun `getWeatherForecast returns ApiError when service returns ApiError`() = runTest {
        val location = "London"
        val apiError = WeatherErrorResponse(Error(1002, "API Limit Exceeded"))

        coEvery { weatherService.getForecast(location) } returns NetworkResponse.ApiError(
            apiError,
            429
        )

        val result = dataSource.getWeatherForecast(location)

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is DomainError.ApiError)
        assertEquals(1002, (error as DomainError.ApiError).code)
    }
}
