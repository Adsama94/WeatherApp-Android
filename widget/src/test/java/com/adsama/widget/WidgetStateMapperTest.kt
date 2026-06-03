package com.adsama.widget

import com.adsama.domain.model.WeatherLocation
import com.adsama.domain.model.WeatherReport
import io.mockk.mockk
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
    fun `map returns correct state when location and report are provided`() {
        // Given
        val mockReport = mockk<WeatherReport> {
            io.mockk.every { forecast } returns listOf(
                mockk {
                    io.mockk.every { date } returns "2023-10-27"
                    io.mockk.every { maxTempC } returns 22.0
                    io.mockk.every { conditionText } returns "Sunny"
                },
                mockk {
                    io.mockk.every { date } returns "2023-10-28"
                    io.mockk.every { maxTempC } returns 23.0
                    io.mockk.every { conditionText } returns "Cloudy"
                },
                mockk {
                    io.mockk.every { date } returns "2023-10-29"
                    io.mockk.every { maxTempC } returns 21.0
                    io.mockk.every { conditionText } returns "Rainy"
                }
            )
        }
        val location = WeatherLocation(
            id = 1,
            name = "London",
            region = "Greater London",
            country = "UK",
            latitude = 51.5,
            longitude = -0.12,
            temperature = 15.6,
            conditionText = "Sunny",
            report = mockReport
        )

        // When
        val result = WidgetStateMapper.map(location)

        // Then
        assertEquals("London", result.locationName)
        assertEquals("15°C", result.currentTemp)
        assertEquals(3, result.forecast.size)
        // Check abbreviated day names (FRI, SAT, SUN for Oct 27, 28, 29 2023)
        assertEquals("FRI", result.forecast[0].day)
        assertEquals("SAT", result.forecast[1].day)
        assertEquals("SUN", result.forecast[2].day)
        assertTrue(!result.isError)
    }
}
