package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteLocationUseCaseTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var useCase: DeleteLocationUseCase

    @Before
    fun setUp() {
        weatherDataSource = mockk()
        useCase = DeleteLocationUseCase(weatherDataSource)
    }

    @Test
    fun `when datasource returns success should emit Success`() = runTest {
        val locationId = 1L
        coEvery { weatherDataSource.deleteLocation(locationId) } returns Result.Success(Unit)

        val results = useCase(locationId).toList()

        assertTrue(results[1] is Result.Success)
    }

    @Test
    fun `when datasource returns error should emit Error`() = runTest {
        val locationId = 1L
        coEvery { weatherDataSource.deleteLocation(locationId) } returns Result.Error(
            DomainError.DatabaseError("Delete failed")
        )

        val results = useCase(locationId).toList()

        assertTrue(results[1] is Result.Error)
    }

}