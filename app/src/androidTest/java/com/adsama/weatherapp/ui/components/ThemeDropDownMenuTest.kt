package com.adsama.weatherapp.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.adsama.weatherapp.ui.theme.WeatherAppTheme
import org.junit.Rule
import org.junit.Test

class ThemeDropDownMenuTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menu_is_displayed() {
        composeTestRule.setContent {
            WeatherAppTheme {
                ThemeDropDownMenu(isDarkMode = false, onToggleTheme = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").assertIsDisplayed()
    }

    @Test
    fun clicking_menu_shows_dropdown_items() {
        composeTestRule.setContent {
            WeatherAppTheme {
                ThemeDropDownMenu(isDarkMode = false, onToggleTheme = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Switch to Dark Mode").assertIsDisplayed()
    }

    @Test
    fun clicking_dropdown_item_triggers_callback() {
        var toggleClicked = false
        composeTestRule.setContent {
            WeatherAppTheme {
                ThemeDropDownMenu(isDarkMode = true, onToggleTheme = { toggleClicked = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithText("Switch to Light Mode").performClick()
        
        assert(toggleClicked)
    }
}
