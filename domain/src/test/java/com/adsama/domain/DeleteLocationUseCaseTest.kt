package com.adsama.domain

import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.Result.Error
import com.adsama.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.every
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
    fun `return validation error when name is blank`() = runTest {
        val location = mockk<WeatherLocation> { every { name } returns "" }
        val results = useCase(location).toList()

        assertTrue(results[1] is Error)
        val error = (results[1] as Error).error
        assertTrue(error is DomainError.ValidationError)
    }

    @Test
    fun `when datasource returns success should emit Success`() = runTest {
        val location = mockk<WeatherLocation> { every { name } returns "London" }
        coEvery { weatherDataSource.deleteLocation(location) } returns Result.Success(Unit)

        val results = useCase(location).toList()

        assertTrue(results[1] is Result.Success)
    }

}