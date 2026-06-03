package com.adsama.weatherapp.ui.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.adsama.domain.model.DomainError
import com.adsama.weatherapp.ui.model.WeatherDetailUiModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import org.junit.Rule
import org.junit.Test

class WeatherDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockDetail = WeatherDetailUiModel(
        locationName = "London",
        currentTemp = "20°C",
        conditionText = "Sunny",
        conditionIcon = "",
        highLowTemp = "H:22° L:15°",
        precipitation = "0%",
        wind = "10km/h",
        windDir = "N",
        uvIndex = "Low",
        sunTimes = "Sunrise: 06:00, Sunset: 20:00",
        hourlyForecast = emptyList(),
        dailyForecast = emptyList(),
        alerts = emptyList()
    )

    @Test
    fun loading_state_shows_progress_indicator() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(isLoading = true),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = {},
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithText("London").assertDoesNotExist()
    }

    @Test
    fun weather_details_are_displayed() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(weather = mockDetail),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = {},
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°C").assertIsDisplayed()
        // Disambiguate if multiple "Sunny" nodes exist
        composeTestRule.onAllNodesWithText("Sunny").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("10km/h").assertIsDisplayed()
    }

    @Test
    fun error_state_shows_error_message() {
        val errorMessage = "Failed to load weather"
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(error = DomainError.UnknownError(errorMessage)),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = {},
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun clicking_back_triggers_callback() {
        var backClicked = false
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(weather = mockDetail),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = { backClicked = true },
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    @Test
    fun save_icon_displayed_when_not_persisted() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(weather = mockDetail, isPersisted = false),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = {},
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Save").assertIsDisplayed()
    }

    @Test
    fun saved_icon_displayed_when_persisted() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailScreen(
                    uiState = DetailUiState(weather = mockDetail, isPersisted = true),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onBack = {},
                    onSaveLocation = {},
                    onRemoveLocation = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Saved").assertIsDisplayed()
    }
}
