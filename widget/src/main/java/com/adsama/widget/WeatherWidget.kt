package com.adsama.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

class WeatherWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeatherWidgetInterface {
        fun weatherDataSource(): WeatherDataSource
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetInterface::class.java
        )
        val dataSource = entryPoint.weatherDataSource()

        val savedLocationsResult = dataSource.getAllSavedLocations().first()
        val location = if (savedLocationsResult is Result.Success) {
            val locations = savedLocationsResult.data
            locations.find { it.name == "Current Location" } ?: locations.firstOrNull()
        } else {
            null
        }

        val widgetState = WidgetStateMapper.map(location)

        provideContent {
            WeatherWidgetContent(widgetState)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun WeatherWidgetContent(state: WeatherWidgetState) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            if (!state.isError) {
                Text(
                    text = state.locationName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(android.R.color.white)
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = state.temperature,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(android.R.color.white)
                    )
                )
                Text(
                    text = state.condition,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(android.R.color.white)
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(
                    text = "Refresh",
                    onClick = actionRunCallback<RefreshAction>()
                )
            } else {
                Text(
                    text = "No Location Saved",
                    style = TextStyle(color = ColorProvider(Color.White))
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(
                    text = "Retry",
                    onClick = actionRunCallback<RefreshAction>()
                )
            }
        }
    }
}
