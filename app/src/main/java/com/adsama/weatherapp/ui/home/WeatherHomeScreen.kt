package com.adsama.weatherapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adsama.database.PersistedWeatherModel
import com.adsama.model.SearchResponse
import com.adsama.weatherapp.R

@Composable
fun WeatherHomeScreen(
    viewModel: WeatherHomeViewModel,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeatherHomeScreen(
        uiState = homeUiState,
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onSearchQueryChange = viewModel::searchLocation,
        onSearchActiveChange = viewModel::updateSearchActive,
        onLocationClick = { locationName ->
            viewModel.updateSearchActive(false)
            onLocationClick(locationName)
        },
        onCurrentLocationClick = {
            viewModel.updateSearchActive(false)
            onCurrentLocationClick()
        },
        onDeleteLocation = viewModel::removeLocationFromSaved
    )
}

@Composable
fun WeatherHomeScreen(
    uiState: HomeUiState,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onDeleteLocation: (Int) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        }
    }

    WeatherHomeContent(
        uiState = uiState,
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onSearchQueryChange = onSearchQueryChange,
        onSearchActiveChange = onSearchActiveChange,
        onLocationClick = onLocationClick,
        onCurrentLocationClick = onCurrentLocationClick,
        onDeleteLocation = onDeleteLocation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeContent(
    uiState: HomeUiState,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onDeleteLocation: (Int) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onSearch = { onSearchActiveChange(false) },
                            expanded = uiState.isSearchActive,
                            onExpandedChange = onSearchActiveChange,
                            placeholder = { Text(stringResource(R.string.etv_hint)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                        )
                    },
                    expanded = uiState.isSearchActive,
                    onExpandedChange = onSearchActiveChange,
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        item {
                            CurrentLocationRow(onClick = onCurrentLocationClick)
                        }
                        itemsIndexed(uiState.searchSuggestions) { _, suggestion ->
                            SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = { onLocationClick(suggestion.name) }
                            )
                        }
                    }
                }
            }

            if ((uiState.isLocalDataLoading || uiState.isSearchLoading) && !uiState.isSearchActive) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.savedLocations.isEmpty()) {
                    WeatherHomeEmptyScreen(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.savedLocations,
                            key = { _, item -> item.locationId }
                        ) { index, location ->
                            val swipeToDismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        onDeleteLocation(index)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = swipeToDismissState,
                                backgroundContent = {
                                    val color = when (swipeToDismissState.dismissDirection) {
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                        else -> Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.delete_white),
                                            contentDescription = "Delete",
                                            tint = Color.White
                                        )
                                    }
                                },
                                enableDismissFromStartToEnd = false
                            ) {
                                SavedLocationItem(
                                    location = location,
                                    freshWeatherData = uiState.freshWeatherData[location.locationId],
                                    isRefreshing = location.locationId in uiState.refreshingLocationIds,
                                    onClick = { onLocationClick(location.name) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentLocationRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.current_location),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.current_location),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SearchSuggestionItem(suggestion: SearchResponse, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = suggestion.name, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(
            text = "${suggestion.region}, ${suggestion.country}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SavedLocationItem(
    location: PersistedWeatherModel,
    freshWeatherData: com.adsama.model.ForecastResponse?,
    isRefreshing: Boolean,
    onClick: () -> Unit
) {
    val displayTemp = freshWeatherData?.current?.temp_c?.toInt() ?: location.temp_c.toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = location.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = location.region,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$displayTemp°C",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherHomeEmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.cloud_sun_rain),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.textview_empty_hint),
            modifier = Modifier.padding(horizontal = 50.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}