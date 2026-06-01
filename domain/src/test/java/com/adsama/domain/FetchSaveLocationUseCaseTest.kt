package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FetchSaveLocationUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var useCase: FetchSaveLocationUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        useCase = FetchSaveLocationUseCase(weatherDataSource)
    }

    @Test
    fun `when datasource emits loading then success, use case should emit same`() = runTest {
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
        val flow = flowOf(Result.Loading<List<WeatherLocation>>(), Result.Success(locations))
        every { weatherDataSource.getAllSavedLocations() } returns flow

        val results = useCase(Unit).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals(locations, (results[1] as Result.Success).data)
    }

    @Test
    fun `when database is empty, should emit success with empty list`() = runTest {
        val flow = flowOf(Result.Success(emptyList<WeatherLocation>()))
        every { weatherDataSource.getAllSavedLocations() } returns flow

        val results = useCase(Unit).toList()

        assertTrue(results[0] is Result.Success)
        assertTrue((results[0] as Result.Success).data.isEmpty())
    }

    @Test
    fun `when database fails, should emit error state`() = runTest {
        val error = DomainError.DatabaseError("Failed to read from DB")
        val flow = flowOf(Result.Error<List<WeatherLocation>>(error))
        every { weatherDataSource.getAllSavedLocations() } returns flow

        val results = useCase(Unit).toList()

        assertTrue(results[0] is Result.Error)
        assertEquals(error, (results[0] as Result.Error).error)
    }

}