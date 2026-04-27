package com.adsama.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.database.PersistedWeatherModel
import com.adsama.domain.DeleteLocationUseCase
import com.adsama.domain.FetchSaveLocationUseCase
import com.adsama.domain.SearchLocationUseCase
import com.adsama.model.Result
import com.adsama.model.SearchResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private var latLongFromGps: String? = null

    private val _savedLocationResults = MutableStateFlow<List<PersistedWeatherModel>>(emptyList())
    val savedLocationResults: StateFlow<List<PersistedWeatherModel>> =
        _savedLocationResults.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<SearchResponse>>(emptyList())
    val searchSuggestionsResult: StateFlow<List<SearchResponse>> = _searchSuggestions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showProgressBar = MutableStateFlow(false)
    val showProgressBar: StateFlow<Boolean> = _showProgressBar.asStateFlow()

    init {
        getAllSavedLocations()
    }

    fun getAllSavedLocations() {
        getSavedLocationUseCase(Unit).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    _savedLocationResults.value = result.data
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value =
                        "Error loading saved locations: ${result.getErrorMessage()}"
                }
            }
        }.launchIn(viewModelScope)
    }

    fun searchLocation(location: String) {
        if (location.isEmpty()) {
            _searchSuggestions.value = emptyList()
            return
        }
        searchLocationUseCase(location).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    _searchSuggestions.value = result.data
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value = "Error searching location: ${result.getErrorMessage()}"
                }
            }
        }.launchIn(viewModelScope)
    }

    fun removeLocationFromSaved(position: Int) {
        val locationToDelete = _savedLocationResults.value.getOrNull(position) ?: return
        deleteLocationUseCase(locationToDelete).onEach { result ->
            when (result) {
                is Result.Loading -> _showProgressBar.value = true
                is Result.Success -> {
                    _showProgressBar.value = false
                    getAllSavedLocations()
                }

                is Result.Error -> {
                    _showProgressBar.value = false
                    _errorMessage.value = "Error deleting location: ${result.getErrorMessage()}"
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setLocationFromGps(latitude: Double, longitude: Double): String {
        val latLong = "$latitude,$longitude"
        latLongFromGps = latLong
        return latLong
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}