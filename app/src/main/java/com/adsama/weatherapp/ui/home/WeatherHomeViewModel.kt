package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.model.AppError
import com.adsama.model.ForecastResponse
import com.adsama.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@Stable
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val saveLocationUseCase: SaveLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val refreshedLocationIds = mutableSetOf<Long>()

    init {
        getAllSavedLocations()
    }

    fun getAllSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _uiState.update {
                        it.copy(isLocalDataLoading = true)
                    }
                }

                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLocalDataLoading = false,
                            savedLocations = result.data
                        )
                    }
                    result.data.forEach { location ->
                        if (location.locationId !in refreshedLocationIds) {
                            refreshWeatherForLocation(location)
                            refreshedLocationIds.add(location.locationId)
                        }
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLocalDataLoading = false,
                            error = AppError.DatabaseError("Error loading saved locations: ${result.getErrorMessage()}")
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun searchLocation(location: String) {
        _uiState.update { it.copy(searchQuery = location) }
        if (location.isEmpty()) {
            _uiState.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        searchLocationUseCase(location).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isSearchLoading = true) }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSearchLoading = false,
                            searchSuggestions = result.data
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSearchLoading = false,
                            error = AppError.NetworkError("Error searching location: ${result.getErrorMessage()}")
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun removeLocationFromSaved(position: Int) {
        val locationToDelete = _uiState.value.savedLocations.getOrNull(position) ?: return
        deleteLocationUseCase(locationToDelete).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLocalDataLoading = true) }
                is Result.Success -> {
                    _uiState.update { it.copy(isLocalDataLoading = false) }
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLocalDataLoading = false,
                            error = AppError.DatabaseError("Error deleting location: ${result.getErrorMessage()}")
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setLocationFromGps(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(latLong = "$latitude,$longitude")
        }
    }

    fun updateSearchActive(isActive: Boolean) {
        _uiState.update { it.copy(isSearchActive = isActive) }
    }

    fun refreshWeatherForLocation(location: PersistedWeatherModel) {
        _uiState.update { it.copy(refreshingLocationIds = it.refreshingLocationIds + location.locationId) }

        fetchCurrentWeatherUseCase(location.name).onEach { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    val freshWeather = result.data
                    _uiState.update {
                        it.copy(
                            freshWeatherData = it.freshWeatherData + (location.locationId to freshWeather),
                            refreshingLocationIds = it.refreshingLocationIds - location.locationId
                        )
                    }
                    // Save the updated weather to database
                    saveLocationUseCase(
                        buildPersistedData(
                            freshWeather,
                            location.locationId
                        )
                    ).launchIn(viewModelScope)
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            refreshingLocationIds = it.refreshingLocationIds - location.locationId,
                            error = AppError.NetworkError("Error refreshing weather: ${result.getErrorMessage()}")
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun buildPersistedData(
        forecastResponse: ForecastResponse,
        locationId: Long
    ): PersistedWeatherModel {
        return PersistedWeatherModel(
            locationId = locationId,
            lat = forecastResponse.location.lat,
            lon = forecastResponse.location.lon,
            name = forecastResponse.location.name ?: "",
            region = forecastResponse.location.region ?: "",
            country = forecastResponse.location.country ?: "",
            temp_c = forecastResponse.current.temp_c,
            text = forecastResponse.current.condition.text,
            icon = forecastResponse.current.condition.icon,
            date = forecastResponse.forecast.forecastday[0].date
        )
    }
}
