package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.weatherapp.ui.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Stable
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _locationEvent = MutableSharedFlow<String>()
    val locationEvent: SharedFlow<String> = _locationEvent.asSharedFlow()

    private val refreshedLocationIds = mutableSetOf<Long>()

    init {
        observeSavedLocations()
    }

    private fun observeSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLocalDataLoading = true) }
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLocalDataLoading = false,
                            savedLocations = result.data.map { it.toUiModel() }
                        )
                    }
                    result.data.forEach { location ->
                        if (location.id !in refreshedLocationIds) {
                            refreshWeatherForLocation(location)
                            refreshedLocationIds.add(location.id)
                        }
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLocalDataLoading = false, error = result.error) }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
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
                            searchSuggestions = result.data.map { it.toUiModel() })
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isSearchLoading = false, error = result.error) }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun removeLocationFromSaved(locationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLocalDataLoading = true) }

            // Temporary reconstruction for deletion logic
            val mockLocation = WeatherLocation(
                id = locationId,
                name = "",
                region = "",
                country = "",
                latitude = 0.0,
                longitude = 0.0
            )

            deleteLocationUseCase(mockLocation).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        refreshedLocationIds.remove(locationId)
                        _uiState.update { it.copy(isLocalDataLoading = false) }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLocalDataLoading = false,
                                error = result.error
                            )
                        }
                    }

                    else -> {}
                }
            }.collect()
        }
    }

    fun updateSearchActive(isActive: Boolean) {
        _uiState.update { it.copy(isSearchActive = isActive) }
    }

    fun fetchLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSearchLoading = true) }
            when (val result = fetchCurrentLocationUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSearchLoading = false) }
                    val location = result.data
                    _locationEvent.emit("${location.latitude},${location.longitude}")
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isSearchLoading = false, error = result.error) }
                }

                is Result.Loading -> {
                    _uiState.update { it.copy(isSearchLoading = true) }
                }
            }
        }
    }

    fun refreshWeatherForLocation(location: WeatherLocation) {
        _uiState.update { it.copy(refreshingLocationIds = it.refreshingLocationIds + location.id) }

        fetchCurrentWeatherUseCase(
            FetchCurrentWeatherUseCase.Params(
                location.name,
                forceRefresh = true
            )
        ).onEach { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    _uiState.update { it.copy(refreshingLocationIds = it.refreshingLocationIds - location.id) }
                }

                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            refreshingLocationIds = it.refreshingLocationIds - location.id,
                            error = result.error
                        ) 
                    }
                }
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }
}
