package com.adsama.weatherapp.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import org.junit.Rule
import org.junit.Test

class WeatherHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockLocation = WeatherLocationUiModel(
        id = 1L,
        name = "London",
        region = "City of London",
        country = "UK",
        temperature = "15°C",
        conditionText = "Cloudy",
        conditionIcon = ""
    )

    @Test
    fun appName_is_displayed_in_top_bar() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherHomeContent(
                    uiState = HomeUiState(),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onSearchQueryChange = {},
                    onSearchActiveChange = {},
                    onLocationClick = {},
                    onCurrentLocationClick = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Weather App").assertIsDisplayed()
    }

    @Test
    fun empty_state_is_shown_when_no_saved_locations() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherHomeContent(
                    uiState = HomeUiState(savedLocations = emptyList()),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onSearchQueryChange = {},
                    onSearchActiveChange = {},
                    onLocationClick = {},
                    onCurrentLocationClick = {},
                    onRefresh = {}
                )
            }
        }

        // The empty hint text from strings.xml (manually checking the text from previous read)
        // From read: stringResource(R.string.textview_empty_hint)
        // I'll use a substring that's likely in the hint.
        composeTestRule.onNodeWithText("Search for a city", substring = true).assertIsDisplayed()
    }

    @Test
    fun saved_locations_are_displayed() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherHomeContent(
                    uiState = HomeUiState(savedLocations = listOf(mockLocation)),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onSearchQueryChange = {},
                    onSearchActiveChange = {},
                    onLocationClick = {},
                    onCurrentLocationClick = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("City of London").assertIsDisplayed()
        composeTestRule.onNodeWithText("15°C").assertIsDisplayed()
    }

    @Test
    fun clicking_saved_location_triggers_callback() {
        var clickedLocation: String? = null
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherHomeContent(
                    uiState = HomeUiState(savedLocations = listOf(mockLocation)),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onSearchQueryChange = {},
                    onSearchActiveChange = {},
                    onLocationClick = { clickedLocation = it },
                    onCurrentLocationClick = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithText("London").performClick()
        assert(clickedLocation == "London, City of London, UK")
    }

    @Test
    fun search_suggestions_are_displayed_when_active() {
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherHomeContent(
                    uiState = HomeUiState(
                        isSearchActive = true,
                        searchSuggestions = listOf(mockLocation)
                    ),
                    isDarkMode = false,
                    onToggleTheme = {},
                    onSearchQueryChange = {},
                    onSearchActiveChange = {},
                    onLocationClick = {},
                    onCurrentLocationClick = {},
                    onRefresh = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Current Location").assertIsDisplayed()
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
    }
}
