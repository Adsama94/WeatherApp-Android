package com.adsama.weatherapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.adsama.weatherapp.R

@Composable
fun ThemeDropDownMenu(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                painter = if (isDarkMode) painterResource(R.drawable.dark_mode) else painterResource(
                    R.drawable.light_mode
                ),
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode") },
                onClick = {
                    onToggleTheme()
                    showMenu = false
                }
            )
        }
    }
}
