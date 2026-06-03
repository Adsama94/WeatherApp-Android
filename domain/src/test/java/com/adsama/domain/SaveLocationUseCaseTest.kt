package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveLocationUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var useCase: SaveLocationUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        useCase = SaveLocationUseCase(weatherDataSource)
    }

    @Test
    fun `when name is blank, should emit validation error`() = runTest {
        val location = mockk<WeatherLocation> {
            every { name } returns ""
        }

        val results = useCase(location).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        val error = (results[1] as Result.Error).error
        assertTrue(error is DomainError.ValidationError)
        assertEquals("Location name cannot be empty", error.message)
    }

    @Test
    fun `when data source saves successfully, should emit success`() = runTest {
        val location = mockk<WeatherLocation> {
            every { name } returns "Paris"
        }
        coEvery { weatherDataSource.saveLocation(location) } returns Result.Success(Unit)

        val results = useCase(location).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
    }

    @Test
    fun `when data source fails to save, should emit error`() = runTest {
        val location = mockk<WeatherLocation> {
            every { name } returns "Paris"
        }
        val dbError = DomainError.DatabaseError("Disk full")
        coEvery { weatherDataSource.saveLocation(location) } returns Result.Error<Unit>(dbError)

        val results = useCase(location).toList()

        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        assertEquals(dbError, (results[1] as Result.Error).error)
    }

}