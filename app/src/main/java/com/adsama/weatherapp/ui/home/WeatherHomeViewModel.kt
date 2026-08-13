package com.adsama.weatherapp.ui.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.DispatcherProvider
import com.adsama.domain.FetchCurrentLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.FetchSavedLocationsPagingUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.domain.WeatherConstants
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.weatherapp.ui.mapper.toUiModel
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
    private val getSavedLocationsPagingUseCase: FetchSavedLocationsPagingUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val fetchCurrentLocationUseCase: FetchCurrentLocationUseCase,
    private val weatherDataSource: WeatherDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val savedLocationsPaging: Flow<PagingData<WeatherLocationUiModel>> =
        getSavedLocationsPagingUseCase(PagingConfig(pageSize = 20))
            .map { pagingData ->
                pagingData.map { it.toUiModel() }
            }
            .cachedIn(viewModelScope)

    private val _locationEvent = MutableSharedFlow<String>()
    val locationEvent: SharedFlow<String> = _locationEvent.asSharedFlow()

    private val currentlyRefreshingIds = mutableSetOf<Long>()
    private val refreshedLocationIds = mutableSetOf<Long>()
    private var lastSavedLocations: List<WeatherLocation> = emptyList()

    init {
        observeSavedLocations()
    }

    private fun observeSavedLocations() {
        getSavedLocationUseCase(Unit)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> {
                        if (lastSavedLocations.isEmpty()) {
                            _isRefreshing.value = true
                        }
                    }

                    is Result.Success -> {
                        lastSavedLocations = result.data

                        val currentTime = System.currentTimeMillis()
                        val locationsToRefresh = result.data.filter { location ->
                            val isOld =
                                (currentTime - location.lastUpdatedEpoch > WeatherConstants.REFRESH_THRESHOLD_MS)
                            val isNotRefreshing = location.id !in currentlyRefreshingIds
                            val hasNotBeenRefreshed = location.id !in refreshedLocationIds

                            isOld && isNotRefreshing && hasNotBeenRefreshed
                        }

                        if (locationsToRefresh.isNotEmpty()) {
                            refreshLocationsBatch(locationsToRefresh)
                        } else if (currentlyRefreshingIds.isEmpty()) {
                            _isRefreshing.value = false
                        }
                    }

                    is Result.Error -> {
                        if (currentlyRefreshingIds.isEmpty()) {
                            _isRefreshing.value = false
                        }
                        _uiState.update { it.copy(error = result.error) }
                    }
                }
            }
            .flowOn(dispatcherProvider.default)
            .launchIn(viewModelScope)
    }

    fun refreshAllLocations(force: Boolean = false) {
        if (lastSavedLocations.isEmpty()) return

        val currentTime = System.currentTimeMillis()
        val locationsToRefresh = lastSavedLocations.filter { location ->
            val isOld =
                force || (currentTime - location.lastUpdatedEpoch > WeatherConstants.REFRESH_THRESHOLD_MS)
            val isNotRefreshing = location.id !in currentlyRefreshingIds

            isOld && isNotRefreshing
        }

        if (locationsToRefresh.isEmpty()) {
            _isRefreshing.value = false
            return
        }

        refreshLocationsBatch(locationsToRefresh)
    }

    private fun refreshLocationsBatch(locations: List<WeatherLocation>) {
        viewModelScope.launch {
            _isRefreshing.value = true
            locations.forEach { currentlyRefreshingIds.add(it.id) }

            val updatedLocations = coroutineScope {
                locations.map { location ->
                    async {
                        val result = fetchCurrentWeatherUseCase(
                            FetchCurrentWeatherUseCase.Params(location.name, forceRefresh = true)
                        ).first { it !is Result.Loading }

                        if (result is Result.Success) {
                            val report = result.data
                            location.copy(
                                temperature = report.current.tempC,
                                conditionText = report.current.conditionText,
                                conditionIcon = report.current.conditionIcon,
                                lastUpdatedEpoch = System.currentTimeMillis(),
                                report = report
                            )
                        } else {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            if (updatedLocations.isNotEmpty()) {
                weatherDataSource.saveLocations(updatedLocations)
            }

            locations.forEach {
                currentlyRefreshingIds.remove(it.id)
                refreshedLocationIds.add(it.id)
            }
            _isRefreshing.value = currentlyRefreshingIds.isNotEmpty()
        }
    }

    fun searchLocation(location: String) {
        _searchQuery.value = location
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
        _isRefreshing.value = true
        currentlyRefreshingIds.add(locationId)

        viewModelScope.launch {
            deleteLocationUseCase(locationId).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        refreshedLocationIds.remove(locationId)
                        currentlyRefreshingIds.remove(locationId)
                        _isRefreshing.value = currentlyRefreshingIds.isNotEmpty()
                    }

                    is Result.Error -> {
                        currentlyRefreshingIds.remove(locationId)
                        _isRefreshing.value = currentlyRefreshingIds.isNotEmpty()
                        _uiState.update { it.copy(error = result.error) }
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

}
