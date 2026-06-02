package com.adsama.weatherapp.ui.mapper

import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import com.adsama.domain.model.CurrentWeather
import com.adsama.domain.model.ForecastDay
import com.adsama.domain.model.HourlyWeather
import com.adsama.domain.model.WeatherAlert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherUiMapperTest {

    @Test
    fun `WeatherLocation toUiModel maps correctly with rounding and icon prefix`() {
        val domainLocation = WeatherLocation(
            id = 1L,
            name = "London",
            region = "London",
            country = "UK",
            latitude = 0.0,
            longitude = 0.0,
            temperature = 15.6,
            conditionText = "Sunny",
            conditionIcon = "//cdn.com/icon.png"
        )

        val result = domainLocation.toUiModel()

        assertEquals("London", result.name)
        assertEquals("15°C", result.temperature)
        assertEquals("https://cdn.com/icon.png", result.conditionIcon)
    }

    @Test
    fun `WeatherReport toDetailUiModel maps nested objects correctly`() {
        val mockLocation = WeatherLocation(name = "London", region = "", country = "", latitude = 0.0, longitude = 0.0)
        val mockCurrent = CurrentWeather(
            tempC = 20.1, feelsLikeC = 19.0, conditionText = "Clear", 
            conditionIcon = "//icon.png", windKph = 10.0, windDir = "N", precipMm = 0.5, uv = 5.0
        )
        val mockForecastDay = ForecastDay(
            date = "2023-10-27", maxTempC = 25.9, minTempC = 15.2, 
            conditionText = "Sunny", conditionIcon = "//icon.png", 
            sunrise = "07:00", sunset = "19:00", hourly = emptyList()
        )
        val mockReport = WeatherReport(
            location = mockLocation,
            current = mockCurrent,
            forecast = listOf(mockForecastDay),
            alerts = emptyList()
        )

        val result = mockReport.toDetailUiModel()

        assertEquals("London", result.locationName)
        assertEquals("20°", result.currentTemp)
        assertEquals("H:25°  L:15°", result.highLowTemp)
        assertEquals("0.5 mm", result.precipitation)
        assertEquals("5.0", result.uvIndex)
        assertTrue(result.conditionIcon.startsWith("https:"))
    }

    @Test
    fun `WeatherAlert toUiModel handles missing event name`() {
        val alert = WeatherAlert(headline = "Big Storm", description = "Rainy", event = null)

        val result = alert.toUiModel()

        assertEquals("Alert", result.event)
        assertEquals("Big Storm", result.headline)
    }

    @Test
    fun `HourlyWeather toUiModel parses time correctly`() {
        val hourly = HourlyWeather(
            time = "2023-10-27 14:00",
            timeEpoch = 123456,
            tempC = 18.0,
            conditionText = "Cloudy",
            conditionIcon = "//icon.png"
        )

        val result = hourly.toUiModel()

        assertEquals("14:00", result.time)
        assertEquals("18°", result.temp)
    }
}
