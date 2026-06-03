package com.adsama.widget

import com.adsama.domain.model.WeatherLocation

data class WeatherWidgetState(
    val locationName: String = "",
    val temperature: String = "--",
    val condition: String = "--",
    val isError: Boolean = false
)

object WidgetStateMapper {
    fun map(location: WeatherLocation?): WeatherWidgetState {
        if (location == null) return WeatherWidgetState(isError = true)
        
        return WeatherWidgetState(
            locationName = location.name,
            temperature = "${location.temperature?.toInt() ?: "--"}°C",
            condition = location.conditionText ?: "--"
        )
    }
}
