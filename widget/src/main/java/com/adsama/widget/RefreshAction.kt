package com.adsama.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

class RefreshAction : ActionCallback {
    
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RefreshActionInterface {
        fun weatherDataSource(): WeatherDataSource
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            RefreshActionInterface::class.java
        )
        val dataSource = entryPoint.weatherDataSource()
        
        // Fetch current locations and refresh the first one for the widget
        val savedLocationsResult = dataSource.getAllSavedLocations().first()
        if (savedLocationsResult is Result.Success) {
            val locations = savedLocationsResult.data
            val locationToRefresh = locations.find { it.name == "Current Location" } ?: locations.firstOrNull()
            
            locationToRefresh?.let {
                dataSource.getForecast(it.name, forceRefresh = true)
            }
        }
        
        // Update the widget UI
        WeatherWidget().updateAll(context)
    }
}
