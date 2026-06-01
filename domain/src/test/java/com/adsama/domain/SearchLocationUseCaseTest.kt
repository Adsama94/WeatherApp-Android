package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchLocationUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var useCase: SearchLocationUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        useCase = SearchLocationUseCase(weatherDataSource)
    }

    @Test
    fun `when query is blank, should emit validation error`() = runTest {
        val query = " "
        val results = useCase(query).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        val error = (results[1] as Result.Error).error
        assertTrue(error is DomainError.ValidationError)
        assertEquals("Search query cannot be empty", error.message)
    }

    @Test
    fun `when data source returns results, should emit success with list`() = runTest {
        val query = "London"
        val locations = listOf(
            WeatherLocation(
                id = 1,
                name = "London",
                region = "Greater London",
                country = "United Kingdom",
                latitude = 51.5,
                longitude = -0.12
            )
        )
        coEvery { weatherDataSource.getSearchResult(query) } returns Result.Success(locations)

        val results = useCase(query).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals(locations, (results[1] as Result.Success).data)
    }

    @Test
    fun `when data source returns no results, should emit success with empty list`() = runTest {
        val query = "UnknownCity"
        coEvery { weatherDataSource.getSearchResult(query) } returns Result.Success(emptyList())

        val results = useCase(query).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertTrue((results[1] as Result.Success).data.isEmpty())
    }

    @Test
    fun `when data source fails, should emit error`() = runTest {
        val query = "London"
        val networkError = DomainError.NetworkError("No internet connection")
        coEvery { weatherDataSource.getSearchResult(query) } returns Result.Error<List<WeatherLocation>>(
            networkError
        )

        val results = useCase(query).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        assertEquals(networkError, (results[1] as Result.Error).error)
    }

}