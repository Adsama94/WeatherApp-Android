package com.adsama.weatherapp.ui.home

import app.cash.turbine.test
import com.adsama.domain.*
import com.adsama.domain.model.DomainError
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
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
class WeatherHomeViewModelTest {

    private val searchLocationUseCase = mockk<SearchLocationUseCase>(relaxed = true)
    private val getSavedLocationUseCase = mockk<FetchSaveLocationUseCase>(relaxed = true)
    private val deleteLocationUseCase = mockk<DeleteLocationUseCase>(relaxed = true)
    private val fetchCurrentWeatherUseCase = mockk<FetchCurrentWeatherUseCase>(relaxed = true)
    private val saveLocationUseCase = mockk<SaveLocationUseCase>(relaxed = true)
    private val fetchCurrentLocationUseCase = mockk<FetchCurrentLocationUseCase>(relaxed = true)

    private lateinit var viewModel: WeatherHomeViewModel
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
        
        viewModel = WeatherHomeViewModel(
            searchLocationUseCase,
            getSavedLocationUseCase,
            deleteLocationUseCase,
            fetchCurrentWeatherUseCase,
            saveLocationUseCase,
            fetchCurrentLocationUseCase,
            testDispatcherProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should observe saved locations`() = runTest {
        val locations = listOf(
            WeatherLocation(1, "London", "", "", 0.0, 0.0, lastUpdatedEpoch = 0L)
        )
        every { getSavedLocationUseCase(Unit) } returns flowOf(Result.Success(locations))
        
        viewModel = WeatherHomeViewModel(
            searchLocationUseCase,
            getSavedLocationUseCase,
            deleteLocationUseCase,
            fetchCurrentWeatherUseCase,
            saveLocationUseCase,
            fetchCurrentLocationUseCase,
            testDispatcherProvider
        )

        advanceTimeBy(300)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(1, state.savedLocations.size)
        assertEquals("London", state.savedLocations[0].name)
    }

    @Test
    fun `searchLocation should update suggestions when successful`() = runTest {
        val query = "New York"
        val suggestions = listOf(
            WeatherLocation(2, "New York", "NY", "USA", 40.7, -74.0)
        )
        every { searchLocationUseCase(query) } returns flowOf(Result.Success(suggestions))

        viewModel.searchLocation(query)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(1, state.searchSuggestions.size)
        assertEquals("New York", state.searchSuggestions[0].name)
    }

    @Test
    fun `searchLocation should update error when it fails`() = runTest {
        val query = "Unknown"
        val error = DomainError.NetworkError("No connection")
        every { searchLocationUseCase(query) } returns flowOf(Result.Error(error))

        viewModel.searchLocation(query)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(error, state.error)
    }

    @Test
    fun `fetchLocation should emit location event on success`() = runTest {
        val mockLocation = WeatherLocation(3, "Current", "", "", 51.5, -0.12)
        coEvery { fetchCurrentLocationUseCase() } returns Result.Success(mockLocation)

        viewModel.locationEvent.test {
            viewModel.fetchLocation()
            runCurrent()
            assertEquals("51.5,-0.12", awaitItem())
        }
    }

    @Test
    fun `updateSearchActive should update uiState`() = runTest {
        viewModel.updateSearchActive(true)
        runCurrent()
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.updateSearchActive(false)
        runCurrent()
        assertTrue(!viewModel.uiState.value.isSearchActive)
    }
}
