package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.domain.WeatherConstants
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.weatherapp.ui.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
@Stable
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _locationEvent = MutableSharedFlow<String>()
    val locationEvent: SharedFlow<String> = _locationEvent.asSharedFlow()

    private val refreshedLocationIds = mutableSetOf<Long>()
    private var lastSavedLocations: List<WeatherLocation> = emptyList()

    init {
        observeSavedLocations()
    }

    private fun observeSavedLocations() {
        getSavedLocationUseCase(Unit)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> _uiState.update { it.copy(isLocalDataLoading = true) }
                    is Result.Success -> {
                        lastSavedLocations = result.data
                        _uiState.update { state ->
                            state.copy(
                                isLocalDataLoading = false,
                                savedLocations = result.data.map { it.toUiModel() }
                            )
                        }
                        val currentTime = System.currentTimeMillis()
                        result.data.forEach { location ->
                            val shouldRefresh = location.id !in refreshedLocationIds ||
                                    (currentTime - location.lastUpdatedEpoch > WeatherConstants.REFRESH_THRESHOLD_MS)

                            if (shouldRefresh) {
                                refreshWeatherForLocation(location)
                                refreshedLocationIds.add(location.id)
                            }
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLocalDataLoading = false,
                                error = result.error
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun refreshAllLocations(force: Boolean = false) {
        if (lastSavedLocations.isEmpty()) return

        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            lastSavedLocations.forEach { location ->
                val shouldRefresh =
                    force || (currentTime - location.lastUpdatedEpoch > WeatherConstants.REFRESH_THRESHOLD_MS)
                if (shouldRefresh) {
                    refreshWeatherForLocation(location)
                }
            }
        }
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
        }.launchIn(viewModelScope)
    }

    fun removeLocationFromSaved(locationId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocalDataLoading = true) }

            deleteLocationUseCase(locationId).onEach { result ->
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
        viewModelScope.launch {
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
        }.launchIn(viewModelScope)
    }
}
