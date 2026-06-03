package com.adsama.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
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
        
        val savedLocationsResult = dataSource.getAllSavedLocations()
            .first { it !is Result.Loading }
            
        val location = if (savedLocationsResult is Result.Success) {
            val locations = savedLocationsResult.data
            locations.find { it.name == "Current Location" } ?: locations.firstOrNull()
        } else {
            null
        }
        
        val widgetState = WidgetStateMapper.map(location)

        provideContent {
            WeatherWidgetContent(context, widgetState)
        }
    }

    @Composable
    private fun WeatherWidgetContent(context: Context, state: WeatherWidgetState) {
        val componentName = ComponentName(context.packageName, "com.adsama.weatherapp.ui.MainActivity")

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.widget_gradient_background))
                .padding(12.dp)
                .clickable(actionStartActivity(componentName)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            if (!state.isError) {
                // Left Side: Current Weather (Compact)
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Horizontal.Start
                ) {
                    Text(
                        text = state.locationName,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(android.R.color.white)
                        )
                    )
                    Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                        Text(
                            text = state.currentTemp,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorProvider(android.R.color.white)
                            )
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        Text(
                            text = state.condition,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = ColorProvider(android.R.color.white)
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.width(8.dp))

                // Right Side: 3-Day Forecast (Horizontal Row)
                Row(
                    modifier = GlanceModifier.wrapContentWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    state.forecast.forEachIndexed { index, forecastDay ->
                        Column(
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                            modifier = GlanceModifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = forecastDay.day,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(android.R.color.white)
                                )
                            )
                            Text(
                                text = forecastDay.temp,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(android.R.color.white)
                                )
                            )
                        }
                        if (index < state.forecast.size - 1) {
                            Spacer(modifier = GlanceModifier.width(4.dp))
                        }
                    }
                }
            } else {
                Text(
                    text = "No Location Saved",
                    style = TextStyle(color = ColorProvider(android.R.color.white))
                )
            }
        }
    }
}
