package com.adsama.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherLocationDAOTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherLocationDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.weatherLocationDAO()
    }

    @After
    fun closeDb() {
        database.close()
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

    @Test
    fun insertAndGetLocation() = runTest {
        dao.insertLocationInfo(mockEntity)
        val allLocations = dao.getAllSavedLocationsOnce()
        assertEquals(1, allLocations.size)
        assertEquals(mockEntity.name, allLocations[0].name)
    }

    @Test
    fun deleteLocation() = runTest {
        dao.insertLocationInfo(mockEntity)
        dao.deleteLocationInfo(mockEntity)
        val allLocations = dao.getAllSavedLocationsOnce()
        assertTrue(allLocations.isEmpty())
    }

    @Test
    fun replaceOnConflict() = runTest {
        dao.insertLocationInfo(mockEntity)
        val updatedEntity = mockEntity.copy(temp_c = 20.0)
        dao.insertLocationInfo(updatedEntity)

        val allLocations = dao.getAllSavedLocationsOnce()
        assertEquals(1, allLocations.size)
        assertEquals(20.0, allLocations[0].temp_c, 0.0)
    }

    @Test
    fun observeLocationsWithFlow() = runTest {
        dao.getAllSavedLocations().test {
            assertTrue(awaitItem().isEmpty())

            dao.insertLocationInfo(mockEntity)
            val listWithOne = awaitItem()
            assertEquals(1, listWithOne.size)
            assertEquals("London", listWithOne[0].name)

            dao.deleteLocationInfo(mockEntity)
            assertTrue(awaitItem().isEmpty())
        }
    }
}
