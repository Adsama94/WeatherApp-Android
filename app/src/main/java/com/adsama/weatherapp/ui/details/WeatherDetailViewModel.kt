package com.adsama.weatherapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.Alert
import com.adsama.model.ForecastDay
import com.adsama.model.ForecastResponse
import com.adsama.model.Hour
import com.adsama.weatherapp.domain.DeleteLocationUseCase
import com.adsama.weatherapp.domain.FetchCurrentWeatherUseCase
import com.adsama.weatherapp.domain.FetchSaveLocationUseCase
import com.adsama.weatherapp.domain.SaveLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val fetchCurrentWeatherUseCase: FetchCurrentWeatherUseCase,
    private val getSavedLocationUseCase: FetchSaveLocationUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    private val mCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private val _forecastResponse = MutableLiveData<ForecastResponse>()
    val forecastResponse: LiveData<ForecastResponse> get() = _forecastResponse

    private val _hourlyResponse = MutableLiveData<List<Hour>>()
    val hourlyResponse: LiveData<List<Hour>> get() = _hourlyResponse

    private val _fiveDayForecastResponse = MutableLiveData<List<ForecastDay>>()
    val fiveDayForecastResponse: LiveData<List<ForecastDay>> get() = _fiveDayForecastResponse

    private val _alertsResponse = MutableLiveData<List<Alert>>()
    val alertsResponse: LiveData<List<Alert>> get() = _alertsResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private var mPersistedDataList: ArrayList<PersistedWeatherModel> = arrayListOf()

    private val _isPersisted = MutableLiveData(false)
    val isPersisted: LiveData<Boolean>
        get() = _isPersisted

    fun getForecastData(location: String) {
        _showProgressBar.value = true
        mCoroutineScope.launch {
            try {
                val result = fetchCurrentWeatherUseCase.executeUseCase(
                    FetchCurrentWeatherUseCase.RequestValues(location)
                )
                _forecastResponse.postValue(result.forecastResponse)
                _hourlyResponse.postValue(result.forecastResponse.forecast.forecastday[0].hour)
                _fiveDayForecastResponse.postValue(result.forecastResponse.forecast.forecastday)
                _alertsResponse.postValue(result.forecastResponse.alerts.alert)
            } catch (t: Throwable) {
                _errorMessage.value = t.message
            }
            _showProgressBar.value = false
            getAllSavedLocations()
        }
    }

    fun saveLocationData() {
        mCoroutineScope.launch {
            saveLocationUseCase.executeUseCase(
                SaveLocationUseCase.RequestValues(buildPersistedData(forecastResponse.value!!))
            )
        }
        getAllSavedLocations()
    }

    private fun getAllSavedLocations() {
        mCoroutineScope.launch {
            val locations =
                getSavedLocationUseCase.executeUseCase(FetchSaveLocationUseCase.RequestValues())
            mPersistedDataList = locations.storedLocations as ArrayList<PersistedWeatherModel>
            for (persistedItem in mPersistedDataList) {
                _isPersisted.value = persistedItem.name == buildPersistedData(forecastResponse.value!!).name
            }
        }
    }

    fun removeLocationFromSaved() {
        mCoroutineScope.launch {
            deleteLocationUseCase.executeUseCase(DeleteLocationUseCase.RequestValues(buildPersistedData(forecastResponse.value!!)))
        }
        getAllSavedLocations()
    }

    private fun buildPersistedData(forecastResponse: ForecastResponse): PersistedWeatherModel {
        return PersistedWeatherModel(
            0,
            forecastResponse.location.lat,
            forecastResponse.location.lon,
            forecastResponse.location.name!!,
            forecastResponse.location.region!!,
            forecastResponse.location.country!!,
            forecastResponse.current.temp_c,
            forecastResponse.current.condition.text,
            forecastResponse.current.condition.icon,
            forecastResponse.forecast.forecastday[0].date
        )
    }

    fun clearVm() {
        onCleared()
    }

}