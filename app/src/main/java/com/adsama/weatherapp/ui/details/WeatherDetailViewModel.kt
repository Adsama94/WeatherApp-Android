package com.adsama.weatherapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
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
class WeatherDetailViewModel @Inject constructor(
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        getAllSavedLocations()
    }

    fun getForecastData(location: String) {
        fetchCurrentWeatherUseCase(location).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    val forecast = result.data
                    val allHours = forecast.forecast.forecastday.take(2).flatMap { it.hour }

                    _uiState.update {
                        it.copy(
                            forecast = forecast,
                            hourlyForecast = allHours,
                            fiveDayForecast = forecast.forecast.forecastday,
                            alerts = forecast.alerts.alert,
                            isLoading = false
                        )
                    }
                    checkIfLocationIsSaved(forecast.location.name ?: "")
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.NetworkError(result.getErrorMessage())
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveLocationData() {
        val forecast = _uiState.value.forecast ?: return
        val persistedModel = buildPersistedData(forecast)
        saveLocationUseCase(persistedModel).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.NetworkError(result.getErrorMessage())
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAllSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, persistedDataList = result.data) }
                    checkIfLocationIsSaved(_uiState.value.forecast?.location?.name ?: "")
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.DatabaseError(result.getErrorMessage())
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkIfLocationIsSaved(locationName: String) {
        _uiState.update { state -> state.copy(isPersisted = _uiState.value.persistedDataList.any { it.name == locationName }) }
    }

    fun removeLocationFromSaved() {
        val locationName = _uiState.value.forecast?.location?.name ?: return
        val locationToDelete =
            _uiState.value.persistedDataList.find { it.name == locationName } ?: return

        deleteLocationUseCase(locationToDelete).onEach { result ->
            when (result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = AppError.DatabaseError(result.getErrorMessage())
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun buildPersistedData(forecastResponse: ForecastResponse): PersistedWeatherModel {
        return PersistedWeatherModel(
            0,
            forecastResponse.location.lat,
            forecastResponse.location.lon,
            forecastResponse.location.name ?: "",
            forecastResponse.location.region ?: "",
            forecastResponse.location.country ?: "",
            forecastResponse.current.temp_c,
            forecastResponse.current.condition.text,
            forecastResponse.current.condition.icon,
            forecastResponse.forecast.forecastday[0].date
        )
    }

}