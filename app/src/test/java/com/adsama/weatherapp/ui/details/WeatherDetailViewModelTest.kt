package com.adsama.weatherapp.ui.details

import app.cash.turbine.test
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.DispatcherProvider
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.domain.model.*
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDetailViewModelTest {

    private val fetchCurrentWeatherUseCase = mockk<FetchCurrentWeatherUseCase>(relaxed = true)
    private val getSavedLocationUseCase = mockk<FetchSaveLocationUseCase>(relaxed = true)
    private val saveLocationUseCase = mockk<SaveLocationUseCase>(relaxed = true)
    private val deleteLocationUseCase = mockk<DeleteLocationUseCase>(relaxed = true)

    private lateinit var viewModel: WeatherDetailViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getSavedLocationUseCase(Unit) } returns flowOf(Result.Success(emptyList()))
        
        viewModel = WeatherDetailViewModel(
            fetchCurrentWeatherUseCase,
            getSavedLocationUseCase,
            saveLocationUseCase,
            deleteLocationUseCase,
            testDispatcherProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getForecastData should update uiState with weather data on success`() = runTest {
        val locationName = "London"
        val mockLocation = WeatherLocation(name = "London", region = "", country = "", latitude = 0.0, longitude = 0.0)
        val mockReport = WeatherReport(
            location = mockLocation,
            current = CurrentWeather(20.0, 19.0, "Sunny", "", 10.0, "N", 0.0, 5.0),
            forecast = emptyList(),
            alerts = emptyList()
        )
        every { fetchCurrentWeatherUseCase(any()) } returns flowOf(Result.Loading(), Result.Success(mockReport))

        viewModel.getForecastData(locationName)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("London", state.weather?.locationName)
        assertEquals("20°", state.weather?.currentTemp)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `getForecastData should update uiState with error on failure`() = runTest {
        val locationName = "Invalid"
        val error = DomainError.ApiError(404, "Not Found")
        every { fetchCurrentWeatherUseCase(any()) } returns flowOf(Result.Loading(), Result.Error(error))

        viewModel.getForecastData(locationName)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(error, state.error)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `saveLocationData should transition through loading and success`() = runTest {
        val mockLocation = WeatherLocation(name = "London", region = "", country = "", latitude = 0.0, longitude = 0.0)
        val mockReport = WeatherReport(
            location = mockLocation,
            current = CurrentWeather(20.0, 19.0, "Sunny", "", 10.0, "N", 0.0, 5.0),
            forecast = emptyList(),
            alerts = emptyList()
        )
        every { fetchCurrentWeatherUseCase(any()) } returns flowOf(Result.Success(mockReport))
        coEvery { saveLocationUseCase(any()) } returns flowOf(Result.Loading(), Result.Success(Unit))

        viewModel.getForecastData("London")
        runCurrent()
        
        viewModel.saveLocationData()
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(!state.isLoading)
    }

    @Test
    fun `checkIfLocationIsSaved should update isPersisted correctly`() = runTest {
        val locationName = "London"
        val mockLocation = WeatherLocation(name = "London", region = "", country = "", latitude = 0.0, longitude = 0.0)
        val mockReport = WeatherReport(
            location = mockLocation,
            current = CurrentWeather(20.0, 19.0, "Sunny", "", 10.0, "N", 0.0, 5.0),
            forecast = emptyList(),
            alerts = emptyList()
        )
        
        every { getSavedLocationUseCase(Unit) } returns flowOf(Result.Success(listOf(mockLocation)))
        
        viewModel = WeatherDetailViewModel(
            fetchCurrentWeatherUseCase,
            getSavedLocationUseCase,
            saveLocationUseCase,
            deleteLocationUseCase,
            testDispatcherProvider
        )
        
        every { fetchCurrentWeatherUseCase(any()) } returns flowOf(Result.Success(mockReport))

        viewModel.getForecastData(locationName)
        runCurrent()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isPersisted)
    }
}
