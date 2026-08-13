package com.adsama.weatherapp.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.flow.flowOf
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
            val savedLocationsFlow = flowOf(PagingData.empty<WeatherLocationUiModel>())
            WeatherAppTheme {
                WeatherHomeContent(
                    searchQuery = "",
                    isSearchActive = false,
                    isSearchLoading = false,
                    searchSuggestions = emptyList(),
                    isRefreshing = false,
                    savedLocationsFlow = savedLocationsFlow,
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
            val savedLocationsFlow = flowOf(PagingData.empty<WeatherLocationUiModel>())
            WeatherAppTheme {
                WeatherHomeContent(
                    searchQuery = "",
                    isSearchActive = false,
                    isSearchLoading = false,
                    searchSuggestions = emptyList(),
                    isRefreshing = false,
                    savedLocationsFlow = savedLocationsFlow,
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

        composeTestRule.onNodeWithText("Search for a city", substring = true).assertIsDisplayed()
    }

    @Test
    fun saved_locations_are_displayed() {
        composeTestRule.setContent {
            val savedLocationsFlow = flowOf(PagingData.from(listOf(mockLocation)))
            WeatherAppTheme {
                WeatherHomeContent(
                    searchQuery = "",
                    isSearchActive = false,
                    isSearchLoading = false,
                    searchSuggestions = emptyList(),
                    isRefreshing = false,
                    savedLocationsFlow = savedLocationsFlow,
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
            val savedLocationsFlow = flowOf(PagingData.from(listOf(mockLocation)))
            WeatherAppTheme {
                WeatherHomeContent(
                    searchQuery = "",
                    isSearchActive = false,
                    isSearchLoading = false,
                    searchSuggestions = emptyList(),
                    isRefreshing = false,
                    savedLocationsFlow = savedLocationsFlow,
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
            val savedLocationsFlow = flowOf(PagingData.empty<WeatherLocationUiModel>())
            WeatherAppTheme {
                WeatherHomeContent(
                    searchQuery = "",
                    isSearchActive = true,
                    isSearchLoading = false,
                    searchSuggestions = listOf(mockLocation),
                    isRefreshing = false,
                    savedLocationsFlow = savedLocationsFlow,
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
