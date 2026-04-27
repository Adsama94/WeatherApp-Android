package com.adsama.weatherapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchCurrentWeatherUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SaveLocationUseCase
import com.adsama.model.Alert
import com.adsama.model.ForecastDay
import com.adsama.model.ForecastResponse
import com.adsama.model.Hour
import com.adsama.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val _forecastResponse = MutableStateFlow<ForecastResponse?>(null)
    val forecastResponse: StateFlow<ForecastResponse?> = _forecastResponse.asStateFlow()

    private val _hourlyResponse = MutableStateFlow<List<Hour>>(emptyList())
    val hourlyResponse: StateFlow<List<Hour>> = _hourlyResponse.asStateFlow()

    private val _fiveDayForecastResponse = MutableStateFlow<List<ForecastDay>>(emptyList())
    val fiveDayForecastResponse: StateFlow<List<ForecastDay>> =
        _fiveDayForecastResponse.asStateFlow()

    private val _alertsResponse = MutableStateFlow<List<Alert>>(emptyList())
    val alertsResponse: StateFlow<List<Alert>> = _alertsResponse.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showProgressBar = MutableStateFlow(false)
    val showProgressBar: StateFlow<Boolean> = _showProgressBar.asStateFlow()

    private var mPersistedDataList: List<PersistedWeatherModel> = emptyList()

    private val _isPersisted = MutableStateFlow(false)
    val isPersisted: StateFlow<Boolean> = _isPersisted.asStateFlow()

    init {
        getAllSavedLocations()
    }

    fun getForecastData(location: String) {
        fetchCurrentWeatherUseCase(location).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    val forecast = result.data
                    _forecastResponse.value = forecast
                    _hourlyResponse.value = forecast.forecast.forecastday[0].hour
                    _fiveDayForecastResponse.value = forecast.forecast.forecastday
                    _alertsResponse.value = forecast.alerts.alert
                    checkIfLocationIsSaved(forecast.location.name ?: "")
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value = result.getErrorMessage()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveLocationData() {
        val forecast = _forecastResponse.value ?: return
        val persistedModel = buildPersistedData(forecast)
        saveLocationUseCase(persistedModel).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value = result.getErrorMessage()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAllSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    mPersistedDataList = result.data
                    _forecastResponse.value?.location?.name?.let { name ->
                        checkIfLocationIsSaved(name)
                    }
                }

                is Result.Error -> {
                    _errorMessage.value = result.getErrorMessage()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkIfLocationIsSaved(locationName: String) {
        _isPersisted.value = mPersistedDataList.any { it.name == locationName }
    }

    fun removeLocationFromSaved() {
        val locationName = _forecastResponse.value?.location?.name ?: return
        val locationToDelete = mPersistedDataList.find { it.name == locationName } ?: return

        deleteLocationUseCase(locationToDelete).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value = result.getErrorMessage()
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}