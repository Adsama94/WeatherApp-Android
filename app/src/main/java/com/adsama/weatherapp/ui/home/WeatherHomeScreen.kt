package com.adsama.weatherapp.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adsama.weatherapp.R
import com.adsama.weatherapp.ui.model.WeatherLocationUiModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme

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
        }
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
    onCurrentLocationClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Log.e("WeatherHomeScreen", "Error: ${error.message}")
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }
    }

    WeatherHomeContent(
        uiState = uiState,
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onSearchQueryChange = onSearchQueryChange,
        onSearchActiveChange = onSearchActiveChange,
        onLocationClick = onLocationClick,
        onCurrentLocationClick = onCurrentLocationClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherHomeContent(
    uiState: HomeUiState,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                DockedSearchBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    inputField = {
                        SearchBarDefaults.InputField(
                            modifier = Modifier.fillMaxWidth(),
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
                        item(contentType = "current_location") {
                            CurrentLocationRow(onClick = onCurrentLocationClick)
                        }
                        itemsIndexed(
                            items = uiState.searchSuggestions,
                            key = { index, it -> "${it.name}_${it.region}_${it.country}_$index" },
                            contentType = { _, _ -> "search_suggestion" }
                        ) { _, suggestion ->
                            SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = { onLocationClick("${suggestion.name}, ${suggestion.region}, ${suggestion.country}") }
                            )
                        }
                    }
                }
            }

            if (uiState.refreshingLocationIds.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.savedLocations.isEmpty()) {
                    WeatherHomeEmptyScreen(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        items(
                            items = uiState.savedLocations,
                            key = { it.id },
                            contentType = { "saved_location" }
                        ) { location ->
                            SavedLocationItem(
                                location = location,
                                onClick = { onLocationClick("${location.name}, ${location.region}, ${location.country}") }
                            )
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
fun SearchSuggestionItem(suggestion: WeatherLocationUiModel, onClick: () -> Unit) {
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
    location: WeatherLocationUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(location.conditionIcon)
                        .crossfade(false)
                        .build(),
                    contentDescription = location.conditionText,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = location.temperature,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
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

@Preview(showBackground = false)
@Composable
fun WeatherHomeScreenPreview() {
    WeatherAppTheme(darkTheme = true) {
        WeatherHomeScreen(
            uiState = HomeUiState(
                searchQuery = "New York",
                isSearchActive = true,
                searchSuggestions = listOf(
                    WeatherLocationUiModel(
                        id = 1L,
                        name = "New York",
                        region = "NY",
                        country = "USA",
                        temperature = "25°C",
                        conditionText = "Sunny",
                        conditionIcon = ""
                    ),
                    WeatherLocationUiModel(
                        id = 2L,
                        name = "Newark",
                        region = "NJ",
                        country = "USA",
                        temperature = "22°C",
                        conditionText = "Cloudy",
                        conditionIcon = ""
                    )
                ),
                savedLocations = emptyList(),
                refreshingLocationIds = emptySet(),
                error = null
            ),
            isDarkMode = true,
            onToggleTheme = {},
            onSearchQueryChange = {},
            onSearchActiveChange = {},
            onLocationClick = {},
            onCurrentLocationClick = {}
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CurrentLocationRowPreview() {
    WeatherAppTheme(darkTheme = true) {
        CurrentLocationRow { }
    }
}

@Preview(showBackground = false)
@Composable
fun SearchSuggestionItemPreview() {
    WeatherAppTheme(darkTheme = true) {
        SearchSuggestionItem(
            suggestion = WeatherLocationUiModel(
                id = 1L,
                name = "New York",
                region = "NY",
                country = "USA",
                temperature = "25°C",
                conditionText = "Sunny",
                conditionIcon = ""
            )
        ) {}
    }
}

@Preview(showBackground = false)
@Composable
fun SavedLocationItemPreview() {
    WeatherAppTheme(darkTheme = true) {
        SavedLocationItem(
            location = WeatherLocationUiModel(
                id = 1L,
                name = "New York",
                region = "NY",
                country = "USA",
                temperature = "25°C",
                conditionText = "Sunny",
                conditionIcon = ""
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = false)
@Composable
fun WeatherHomeEmptyScreenPreview() {
    WeatherAppTheme(darkTheme = true) {
        WeatherHomeEmptyScreen()
    }
}
