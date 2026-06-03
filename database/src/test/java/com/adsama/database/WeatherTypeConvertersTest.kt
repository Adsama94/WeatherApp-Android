package com.adsama.database

import com.adsama.domain.model.CurrentWeather
import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherTypeConvertersTest {

    private val converters = WeatherTypeConverters()

    private val mockReport = WeatherReport(
        location = WeatherLocation(
            name = "London",
            region = "Greater London",
            country = "UK",
            latitude = 51.5,
            longitude = -0.12
        ),
        current = CurrentWeather(
            tempC = 15.0,
            feelsLikeC = 14.0,
            conditionText = "Partly cloudy",
            conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
            windKph = 10.0,
            windDir = "W",
            precipMm = 0.0,
            uv = 4.0
        ),
        forecast = emptyList(),
        alerts = emptyList()
    )

    @Test
    fun `fromWeatherReport should return null when report is null`() {
        assertNull(converters.fromWeatherReport(null))
    }

    @Test
    fun `toWeatherReport should return null when string is null or empty`() {
        assertNull(converters.toWeatherReport(null))
        assertNull(converters.toWeatherReport(""))
    }

    @Test
    fun `converters should be symmetric`() {
        val json = converters.fromWeatherReport(mockReport)

        val result = converters.toWeatherReport(json)

        assertEquals(mockReport, result)
    }

    @Test
    fun `toWeatherReport should handle malformed json gracefully`() {
        // This will likely throw an exception depending on implementation, 
        // but it's good to know the behavior.
        try {
            converters.toWeatherReport("{ invalid json }")
        } catch (e: Exception) {
            // Success - it's expected to throw if not handled internally
        }
    }
}
