package com.adsama.weatherapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.SearchResponse
import com.adsama.weatherapp.domain.DeleteLocationUseCase
import com.adsama.weatherapp.domain.FetchSaveLocationUseCase
import com.adsama.weatherapp.domain.SearchLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val searchLocationUseCase: SearchLocationUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val mCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private lateinit var latLongFromGps: String
    private val _savedLocationResults = MutableLiveData<ArrayList<PersistedWeatherModel>>()
    val savedLocationResults: LiveData<ArrayList<PersistedWeatherModel>> get() = _savedLocationResults

    private val _searchSuggestions = MutableLiveData<List<SearchResponse>>()
    val searchSuggestionsResult: LiveData<List<SearchResponse>> get() = _searchSuggestions

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getAllSavedLocations() {
        mCoroutineScope.launch {
            try {
                getSavedLocationUseCase.useCaseCallback =
                    object : FetchSaveLocationUseCase.FetchSaveLocationCallback {
                        override fun onSuccess(response: FetchSaveLocationUseCase.ResponseValue) {
                            _savedLocationResults.value =
                                response.storedLocations as ArrayList<PersistedWeatherModel>
                        }

                        override fun onError(t: Throwable) {
                            _errorMessage.value = "Error loading saved locations: ${t.message}"
                        }
                    }
                getSavedLocationUseCase.executeUseCase(FetchSaveLocationUseCase.RequestValues())
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun searchLocation(location: String) {
        mCoroutineScope.launch {
            try {
                searchLocationUseCase.useCaseCallback =
                    object : SearchLocationUseCase.SearchLocationCallbacks {
                        override fun onSuccess(response: SearchLocationUseCase.ResponseValue) {
                            _searchSuggestions.value = response.searchResponse
                        }

                        override fun onError(t: Throwable) {
                            _errorMessage.value = "Error loading saved locations: ${t.message}"
                        }

                    }
                searchLocationUseCase.executeUseCase(SearchLocationUseCase.RequestValues(location))
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun removeLocationFromSaved(position: Int) {
        mCoroutineScope.launch {
            deleteLocationUseCase.executeUseCase(
                DeleteLocationUseCase.RequestValues(
                    _savedLocationResults.value!![position]
                )
            )
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