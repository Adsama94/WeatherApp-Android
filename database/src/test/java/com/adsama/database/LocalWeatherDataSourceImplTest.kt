package com.adsama.database

import app.cash.turbine.test
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalWeatherDataSourceImplTest {

    private lateinit var dao: WeatherLocationDAO
    private lateinit var dataSource: LocalWeatherDataSourceImpl

    @Before
    fun setUp() {
        dao = mockk()
        dataSource = LocalWeatherDataSourceImpl(dao)
    }

    private val mockEntity = PersistedWeatherModel(
        locationId = 1,
        lat = 51.5,
        lon = -0.12,
        name = "London",
        region = "City of London",
        country = "UK",
        temp_c = 15.0,
        text = "Cloudy",
        icon = "icon_url",
        date = "2023-10-27",
        lastUpdatedEpoch = 123456789L,
        report = null
    )

    private val mockDomainLocation = WeatherLocation(
        id = 1,
        name = "London",
        region = "City of London",
        country = "UK",
        latitude = 51.5,
        longitude = -0.12,
        temperature = 15.0,
        conditionText = "Cloudy",
        conditionIcon = "icon_url",
        lastUpdated = "2023-10-27",
        lastUpdatedEpoch = 123456789L,
        report = null
    )

    @Test
    fun `fetchSavedLocations should emit Loading then Success with mapped data`() = runTest {
        every { dao.getAllSavedLocations() } returns flowOf(listOf(mockEntity))

        dataSource.fetchSavedLocations().test {
            assertTrue(awaitItem() is Result.Loading)
            val success = awaitItem() as Result.Success
            assertEquals(1, success.data.size)
            assertEquals(mockDomainLocation, success.data[0])
            awaitComplete()
        }
    }

    @Test
    fun `fetchSavedLocations should emit Error when DAO throws exception`() = runTest {
        val errorMessage = "Database failure"
        every { dao.getAllSavedLocations() } returns kotlinx.coroutines.flow.flow {
            throw Exception(errorMessage)
        }

        dataSource.fetchSavedLocations().test {
            assertTrue(awaitItem() is Result.Loading)
            val error = awaitItem() as Result.Error
            assertTrue(error.error is DomainError.DatabaseError)
            assertEquals(errorMessage, error.error.message)
            awaitComplete()
        }
    }

    @Test
    fun `saveLocation should return Success when DAO succeeds`() = runTest {
        coEvery { dao.insertLocationInfo(any()) } returns Unit

        val result = dataSource.saveLocation(mockDomainLocation)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `saveLocation should return Error when DAO fails`() = runTest {
        coEvery { dao.insertLocationInfo(any()) } throws Exception("Save failed")

        val result = dataSource.saveLocation(mockDomainLocation)

        assertTrue(result is Result.Error)
        assertEquals("Save failed", (result as Result.Error).error.message)
    }

    @Test
    fun `fetchForecast matches location by name precisely`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } returns listOf(mockEntity)

        val result = dataSource.fetchForecast("London")

        assertTrue(result is Result.Success)
        assertEquals("London", (result as Result.Success).data.name)
    }

    @Test
    fun `fetchForecast matches location by composite name`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } returns listOf(mockEntity)

        val result = dataSource.fetchForecast("London, City of London, UK")

        assertTrue(result is Result.Success)
        assertEquals("London", (result as Result.Success).data.name)
    }

    @Test
    fun `fetchForecast matches location by coordinates`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } returns listOf(mockEntity)

        val result = dataSource.fetchForecast("51.5, -0.12")

        assertTrue(result is Result.Success)
        assertEquals("London", (result as Result.Success).data.name)
    }

    @Test
    fun `fetchForecast returns Error when no match found`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } returns listOf(mockEntity)

        val result = dataSource.fetchForecast("Paris")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error.message.contains("No cached forecast found"))
    }

    @Test
    fun `fetchForecast handles malformed coordinates gracefully`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } returns listOf(mockEntity)

        val result1 = dataSource.fetchForecast("invalid, coordinates")
        val result2 = dataSource.fetchForecast("51.5, not_a_number")
        val result3 = dataSource.fetchForecast("51.5") 

        assertTrue(result1 is Result.Error)
        assertTrue(result2 is Result.Error)
        assertTrue(result3 is Result.Error)
    }

    @Test
    fun `fetchForecast handles database exception`() = runTest {
        coEvery { dao.getAllSavedLocationsOnce() } throws Exception("DB error")

        val result = dataSource.fetchForecast("London")

        assertTrue(result is Result.Error)
        assertEquals("DB error", (result as Result.Error).error.message)
    }

    @Test
    fun `deleteLocation should return Error when DAO fails`() = runTest {
        coEvery { dao.deleteLocationInfo(any()) } throws Exception("Delete failed")

        val result = dataSource.deleteLocation(mockDomainLocation)

        assertTrue(result is Result.Error)
        assertEquals("Delete failed", (result as Result.Error).error.message)
    }
}
