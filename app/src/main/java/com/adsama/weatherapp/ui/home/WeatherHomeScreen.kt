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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
    onCurrentLocationClick: () -> Unit
) {
    val savedLocations by viewModel.savedLocationResults.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestionsResult.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.showProgressBar.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    WeatherHomeContent(
        searchQuery = searchQuery,
        onSearchQueryChange = {
            searchQuery = it
            viewModel.searchLocation(it)
        },
        isSearchActive = isSearchActive,
        onSearchActiveChange = { isSearchActive = it },
        savedLocations = savedLocations,
        searchSuggestions = searchSuggestions,
        isLoading = isLoading,
        onLocationClick = { locationName ->
            isSearchActive = false
            onLocationClick(locationName)
        },
        onCurrentLocationClick = {
            isSearchActive = false
            onCurrentLocationClick()
        },
        onDeleteLocation = { index ->
            viewModel.removeLocationFromSaved(index)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    savedLocations: List<PersistedWeatherModel>,
    searchSuggestions: List<SearchResponse>,
    isLoading: Boolean,
    onLocationClick: (String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onDeleteLocation: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onSearch = { onSearchActiveChange(false) },
                            expanded = isSearchActive,
                            onExpandedChange = onSearchActiveChange,
                            placeholder = { Text(stringResource(R.string.etv_hint)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                        )
                    },
                    expanded = isSearchActive,
                    onExpandedChange = onSearchActiveChange
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        item {
                            CurrentLocationRow(onClick = onCurrentLocationClick)
                        }
                        itemsIndexed(searchSuggestions) { _, suggestion ->
                            SearchSuggestionItem(
                                suggestion = suggestion,
                                onClick = { onLocationClick(suggestion.name) }
                            )
                        }
                    }
                }
            }

            if (isLoading && !isSearchActive) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (savedLocations.isEmpty()) {
                    WeatherHomeEmptyScreen(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        itemsIndexed(
                            items = savedLocations,
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
                                        SwipeToDismissBoxValue.EndToStart -> Color.Red
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
            tint = Color(0xFF3B60E4),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.current_location),
            color = Color(0xFF3B60E4),
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
        Text(text = suggestion.name, fontSize = 16.sp)
        Text(
            text = "${suggestion.region}, ${suggestion.country}",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SavedLocationItem(location: PersistedWeatherModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = location.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = location.region, fontSize = 14.sp, color = Color.Gray)
            }
            Text(text = "${location.temp_c.toInt()}°C", fontSize = 24.sp)
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
            color = Color(0xFFBEBEBE)
        )
    }
}