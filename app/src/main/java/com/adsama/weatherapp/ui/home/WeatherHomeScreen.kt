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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.adsama.weatherapp.R
import com.adsama.weatherapp.ui.components.ThemeDropDownMenu
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

    val onLocationClickInternal = remember(viewModel, onLocationClick) {
        { locationName: String ->
            viewModel.updateSearchActive(false)
            onLocationClick(locationName)
        }
    }

    val onCurrentLocationClickInternal = remember(viewModel, onCurrentLocationClick) {
        {
            viewModel.updateSearchActive(false)
            onCurrentLocationClick()
        }
    }

    WeatherHomeScreen(
        uiState = homeUiState,
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onSearchQueryChange = viewModel::searchLocation,
        onSearchActiveChange = viewModel::updateSearchActive,
        onLocationClick = onLocationClickInternal,
        onCurrentLocationClick = onCurrentLocationClickInternal,
        onRefresh = viewModel::refreshAllLocations
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
    onRefresh: () -> Unit
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
        onCurrentLocationClick = onCurrentLocationClick,
        onRefresh = onRefresh
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
    onCurrentLocationClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            WeatherHomeTopAppBar(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
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

            WeatherSearchBarSection(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                isActive = uiState.isSearchActive,
                onActiveChange = onSearchActiveChange,
                suggestions = uiState.searchSuggestions,
                onLocationClick = onLocationClick,
                onCurrentLocationClick = onCurrentLocationClick
            )

            val isRefreshingInternal = remember(uiState.refreshingLocationIds, uiState.isLocalDataLoading) {
                uiState.refreshingLocationIds.isNotEmpty() || uiState.isLocalDataLoading
            }

            WeatherSavedLocationsSection(
                savedLocations = uiState.savedLocations,
                isLocalDataLoading = isRefreshingInternal,
                onRefresh = onRefresh,
                onLocationClick = onLocationClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherHomeTopAppBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
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
            ThemeDropDownMenu(
                isDarkMode = isDarkMode,
                onToggleTheme = onToggleTheme
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherSearchBarSection(
    query: String,
    onQueryChange: (String) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    suggestions: List<WeatherLocationUiModel>,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        DockedSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .testTag("search_bar"),
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.fillMaxWidth(),
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { onActiveChange(false) },
                    expanded = isActive,
                    onExpandedChange = onActiveChange,
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
            expanded = isActive,
            onExpandedChange = onActiveChange,
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
                    items = suggestions,
                    key = { index, it -> "${it.name}_${it.region}_${it.country}_$index" },
                    contentType = { _, _ -> "search_suggestion" }
                ) { _, suggestion ->
                    SearchSuggestionItem(
                        suggestion = suggestion,
                        onLocationClick = onLocationClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherSavedLocationsSection(
    savedLocations: List<WeatherLocationUiModel>,
    isLocalDataLoading: Boolean,
    onRefresh: () -> Unit,
    onLocationClick: (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isLocalDataLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (savedLocations.isEmpty()) {
            WeatherHomeEmptyScreen(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .testTag("saved_locations_list"),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(
                    items = savedLocations,
                    key = { it.id },
                    contentType = { "saved_location" }
                ) { location ->
                    SavedLocationItem(
                        location = location,
                        onLocationClick = onLocationClick
                    )
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
fun SearchSuggestionItem(
    suggestion: WeatherLocationUiModel,
    onLocationClick: (String) -> Unit
) {
    val clickAction =
        remember(suggestion.name, suggestion.region, suggestion.country, onLocationClick) {
            { onLocationClick("${suggestion.name}, ${suggestion.region}, ${suggestion.country}") }
        }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = clickAction)
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
    onLocationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clickAction = remember(location.name, location.region, location.country, onLocationClick) {
        { onLocationClick("${location.name}, ${location.region}, ${location.country}") }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = clickAction)
            .testTag("saved_location_item_${location.id}"),
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
                    model = location.conditionIcon,
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
            onCurrentLocationClick = {},
            onRefresh = {}
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
            ),
            onLocationClick = {}
        )
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
            onLocationClick = {}
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
