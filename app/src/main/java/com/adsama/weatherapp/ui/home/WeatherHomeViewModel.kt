package com.adsama.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.SearchResponse
import com.adsama.weatherapp.domain.DeleteLocationUseCase
import com.adsama.weatherapp.domain.FetchSaveLocationUseCase
import com.adsama.weatherapp.domain.SearchLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private lateinit var latLongFromGps: String
    private val _savedLocationResults = MutableLiveData<ArrayList<PersistedWeatherModel>>()
    val savedLocationResults: LiveData<ArrayList<PersistedWeatherModel>> get() = _savedLocationResults

    private val _searchSuggestions = MutableLiveData<List<SearchResponse>>()
    val searchSuggestionsResult: LiveData<List<SearchResponse>> get() = _searchSuggestions

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getAllSavedLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val locations = getSavedLocationUseCase.executeUseCase(FetchSaveLocationUseCase.RequestValues())
            _savedLocationResults.postValue(locations.storedLocations as ArrayList<PersistedWeatherModel>)
        }
    }

    fun searchLocation(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val searchSuggestions = searchLocationUseCase.executeUseCase(SearchLocationUseCase.RequestValues(location))
            _searchSuggestions.postValue(searchSuggestions.searchResponse)
        }
    }

    fun removeLocationFromSaved(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteLocationUseCase.executeUseCase(DeleteLocationUseCase.RequestValues(
                _savedLocationResults.value!![position]
            ))
            getAllSavedLocations()
        }
    }

    fun setLocationFromGps(latitude: Double, longitude: Double): String {
        latLongFromGps = "$latitude,$longitude"
        return latLongFromGps
    }

    fun clearVm() {
        onCleared()
    }

}