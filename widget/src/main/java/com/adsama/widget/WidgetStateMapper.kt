package com.adsama.widget

import com.adsama.domain.model.WeatherLocation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ForecastDayState(
    val day: String,
    val temp: String
)

data class WeatherWidgetState(
    val locationName: String = "",
    val currentTemp: String = "--",
    val condition: String = "--",
    val forecast: List<ForecastDayState> = emptyList(),
    val isError: Boolean = false
)

object WidgetStateMapper {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    fun map(location: WeatherLocation?): WeatherWidgetState {
        if (location == null || location.report == null) return WeatherWidgetState(isError = true)
        
        val report = location.report!!
        val forecastDays = report.forecast.take(3).map { day ->
            val dayName = try {
                val date = LocalDate.parse(day.date, dateFormatter)
                date.dayOfWeek.name.take(3) // MON, TUE, etc.
            } catch (e: Exception) {
                day.date.takeLast(5)
            }

            ForecastDayState(
                day = dayName,
                temp = "${day.maxTempC.toInt()}°"
            )
        }

        return WeatherWidgetState(
            locationName = location.name,
            currentTemp = "${location.temperature?.toInt() ?: "--"}°C",
            condition = location.conditionText ?: "--",
            forecast = forecastDays
        )
    }
}
