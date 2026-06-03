package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherReport
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FetchCurrentWeatherUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var useCase: FetchCurrentWeatherUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        useCase = FetchCurrentWeatherUseCase(weatherDataSource)
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
    fun `execute returns success when dataSource returns success`() = runTest {
        val locationName = "London"
        val mockReport = mockk<WeatherReport>()
        coEvery { weatherDataSource.getForecast(locationName, false) } returns Result.Success(mockReport)

        val result = useCase.invoke(FetchCurrentWeatherUseCase.Params(locationName)).last()

        assertTrue(result is Result.Success<*>)
        assertEquals(mockReport, (result as Result.Success<WeatherReport>).data)
    }

    @Test
    fun `execute returns error when dataSource returns error`() = runTest {
        val locationName = "London"
        val mockError = DomainError.NetworkError("No Internet")
        coEvery { weatherDataSource.getForecast(locationName, false) } returns Result.Error(mockError)

        val result = useCase.invoke(FetchCurrentWeatherUseCase.Params(locationName)).last()

        assertTrue(result is Result.Error<*>)
        assertEquals(mockError, (result as Result.Error<*>).error)
    }

}