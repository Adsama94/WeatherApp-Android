package com.adsama.network.mapper

import com.adsama.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherRemoteMapperTest {

    private val mapper = WeatherRemoteMapper()

    @Test
    fun `mapSearchResponseToDomain maps all fields correctly`() {
        val searchResponse = SearchResponse(
            name = "London",
            region = "Greater London",
            country = "United Kingdom",
            lat = 51.52,
            lon = -0.11
        )

        val result = mapper.mapSearchResponseToDomain(searchResponse)

        assertEquals("London", result.name)
        assertEquals("Greater London", result.region)
        assertEquals("United Kingdom", result.country)
        assertEquals(51.52, result.latitude, 0.0)
        assertEquals(-0.11, result.longitude, 0.0)
    }

    @Test
    fun `mapForecastResponseToDomain maps all fields correctly`() {
        val condition = Condition(text = "Sunny", icon = "//icon.png")
        val forecastResponse = ForecastResponse(
            location = Location(
                name = "London",
                region = "City of London",
                country = "UK",
                lat = 51.5,
                lon = -0.12
            ),
            current = Current(
                temp_c = 20.0,
                feelslike_c = 19.0,
                condition = condition,
                wind_kph = 10.0,
                wind_dir = "N",
                precip_mm = 0.0,
                uv = 5.0
            ),
            forecast = Forecast(
                forecastday = listOf(
                    ForecastDay(
                        date = "2023-10-27",
                        day = Day(
                            maxtemp_c = 22.0,
                            mintemp_c = 15.0,
                            condition = condition
                        ),
                        astro = Astro(sunrise = "07:00 AM", sunset = "06:00 PM"),
                        hour = listOf(
                            Hour(
                                time = "2023-10-27 10:00",
                                time_epoch = 1698393600,
                                temp_c = 18.0,
                                condition = condition
                            )
                        )
                    )
                )
            ),
            alerts = Alerts(
                alert = listOf(
                    Alert(headline = "Headline", desc = "Description", event = "Event")
                )
            )
        )

        val result = mapper.mapForecastResponseToDomain(forecastResponse)

        assertEquals("London", result.location.name)
        assertEquals(20.0, result.current.tempC, 0.0)
        assertEquals(1, result.forecast.size)
        assertEquals("2023-10-27", result.forecast[0].date)
        assertEquals(1, result.forecast[0].hourly.size)
        assertEquals(18.0, result.forecast[0].hourly[0].tempC, 0.0)
        assertEquals(1, result.alerts.size)
        assertEquals("Headline", result.alerts[0].headline)
    }
}
