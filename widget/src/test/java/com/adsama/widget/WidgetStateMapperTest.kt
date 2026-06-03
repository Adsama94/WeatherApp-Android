package com.adsama.widget

import com.adsama.domain.model.WeatherLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetStateMapperTest {

    @Test
    fun `map returns error state when location is null`() {
        val result = WidgetStateMapper.map(null)
        assertTrue(result.isError)
    }

    @Test
    fun `map returns correct state when location is provided`() {
        // Given
        val location = WeatherLocation(
            id = 1,
            name = "London",
            region = "Greater London",
            country = "UK",
            latitude = 51.5,
            longitude = -0.12,
            temperature = 15.6,
            conditionText = "Sunny"
        )

        // When
        val result = WidgetStateMapper.map(location)

        // Then
        assertEquals("London", result.locationName)
        assertEquals("15°C", result.temperature)
        assertEquals("Sunny", result.condition)
        assertTrue(!result.isError)
    }

    @Test
    fun `map handles null temperature gracefully`() {
        // Given
        val location = WeatherLocation(
            id = 1,
            name = "London",
            region = "",
            country = "",
            latitude = 0.0,
            longitude = 0.0,
            temperature = null,
            conditionText = "Cloudy"
        )

        // When
        val result = WidgetStateMapper.map(location)

        // Then
        assertEquals("--°C", result.temperature)
    }
}
