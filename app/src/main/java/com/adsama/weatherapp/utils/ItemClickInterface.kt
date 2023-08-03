package com.adsama.weatherapp.utils

import com.adsama.database.PersistedWeatherModel
import com.adsama.model.SearchResponse

interface ItemClickInterface {

    interface SearchSuggestionInterface : ItemClickInterface {
        fun getSelectedSuggestionClick(searchResponse: SearchResponse, position: Int)
    }

    interface SelectedLocationInterface : ItemClickInterface {
        fun getSelectedLocationClick(persistedWeatherModel: PersistedWeatherModel, position: Int)
    }

}