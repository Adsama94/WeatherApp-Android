package com.adsama.weatherapp.ui.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.weatherapp.ui.mapper.toDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Stable
class WeatherDetailViewModel @Inject constructor(
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentReport: WeatherReport? = null
    private var savedLocations: List<WeatherLocation> = emptyList()

    init {
        observeSavedLocations()
    }

    private fun observeSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Success -> {
                    savedLocations = result.data
                    checkIfLocationIsSaved()
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun getForecastData(location: String) {
        fetchCurrentWeatherUseCase(
            FetchCurrentWeatherUseCase.Params(
                location,
                forceRefresh = false
            )
        ).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    currentReport = result.data
                    _uiState.update {
                        it.copy(
                            weather = result.data.toDetailUiModel(),
                            isLoading = false
                        )
                    }
                    checkIfLocationIsSaved()
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveLocationData() {
        val report = currentReport ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            saveLocationUseCase(report.location.copy(report = report)).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.error) }
                    }

                    else -> {}
                }
            }.collect()
        }
    }

    private fun checkIfLocationIsSaved() {
        val locationName = currentReport?.location?.name ?: return
        val isSaved = savedLocations.any { it.name == locationName }
        _uiState.update { it.copy(isPersisted = isSaved) }
    }

    fun removeLocationFromSaved() {
        val locationName = currentReport?.location?.name ?: return
        val locationToDelete = savedLocations.find { it.name == locationName } ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deleteLocationUseCase(locationToDelete.id).onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false, isPersisted = false) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.error) }
                    }

                    else -> {}
                }
            }.collect()
        }
    }
}
