package com.adsama.weatherapp.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.adsama.weatherapp.R
import com.adsama.weatherapp.ui.model.AlertUiModel
import com.adsama.weatherapp.ui.model.DailyForecastUiModel
import com.adsama.weatherapp.ui.model.HourlyForecastUiModel
import com.adsama.weatherapp.ui.model.WeatherDetailUiModel
import com.adsama.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherDetailScreen(
    locationName: String,
    viewModel: WeatherDetailViewModel,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    val detailUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(locationName) {
        viewModel.getForecastData(locationName)
    }

    WeatherDetailScreen(
        uiState = detailUiState,
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onBack = onBack,
        onSaveLocation = viewModel::saveLocationData,
        onRemoveLocation = viewModel::removeLocationFromSaved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    uiState: DetailUiState,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    onSaveLocation: () -> Unit,
    onRemoveLocation: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.weather?.locationName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.weather != null) {
                        IconButton(onClick = {
                            if (uiState.isPersisted) onRemoveLocation()
                            else onSaveLocation()
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (uiState.isPersisted) R.drawable.bookmark_remove
                                    else R.drawable.bookmark_add
                                ),
                                contentDescription = if (uiState.isPersisted) "Saved" else "Save"
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = if (isDarkMode) painterResource(R.drawable.dark_mode) else painterResource(
                                    R.drawable.light_mode
                                ),
                                contentDescription = "Menu"
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
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (uiState.isLoading && uiState.weather == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (uiState.error != null && uiState.weather == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cloud_sun_rain),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error.message,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                uiState.weather?.let {
                    WeatherDetailContent(uiModel = it)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailContent(
    uiModel: WeatherDetailUiModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(contentType = "header") {
            WeatherHeader(uiModel)
        }
        item(contentType = "telemetry") {
            TelemetrySection(uiModel)
        }
        item(contentType = "hourly") {
            HourlySection(uiModel.conditionText, uiModel.hourlyForecast)
        }
        item(contentType = "five_day") {
            FiveDayForecastSection(uiModel.dailyForecast)
        }
        if (uiModel.alerts.isNotEmpty()) {
            item(contentType = "alerts") {
                AlertsSection(uiModel.alerts)
            }
        }
    }
}

@Composable
fun WeatherHeader(data: WeatherDetailUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp), contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = data.conditionIcon,
                contentDescription = data.conditionText,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = data.currentTemp,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = data.conditionText,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = data.highLowTemp,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TelemetrySection(data: WeatherDetailUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.rainy,
                label = stringResource(R.string.precipitation),
                value = data.precipitation
            )
            TelemetryCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.air,
                label = stringResource(R.string.wind),
                value = data.wind,
                extra = data.windDir
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.outline_wb_sunny,
                label = stringResource(R.string.uv_index),
                value = data.uvIndex
            )
            TelemetryCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = R.drawable.sunny,
                label = stringResource(R.string.sun),
                value = data.sunTimes
            )
        }
    }
}

@Composable
fun TelemetryCard(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    value: String,
    extra: String? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (extra != null) {
                    Text(
                        text = extra,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun HourlySection(condition: String, hourly: List<HourlyForecastUiModel>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = condition,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            val currentTimeEpoch = (System.currentTimeMillis() / 1000).toInt()
            val next12HoursEpoch = currentTimeEpoch + (12 * 3600) // 12 hours in seconds
            val filteredHourly =
                hourly.filter { it.timeEpoch in currentTimeEpoch..next12HoursEpoch }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = filteredHourly,
                    key = { it.timeEpoch },
                    contentType = { "hourly_forecast" }
                ) { hour ->
                    HourlyItem(hour)
                }
            }
        }
    }
}

@Composable
fun HourlyItem(hour: HourlyForecastUiModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = hour.time,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = hour.icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = hour.temp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FiveDayForecastSection(forecast: List<DailyForecastUiModel>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.calendar_forecast),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.five_day_forecast),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            forecast.forEach { day ->
                ForecastDayItem(day)
            }
        }
    }
}

@Composable
fun ForecastDayItem(day: DailyForecastUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = day.day,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = day.date,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = day.icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = day.highLowTemp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AlertsSection(alerts: List<AlertUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.notifications),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.alerts),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        alerts.forEach { alert ->
            AlertItem(alert)
        }
    }
}

@Composable
fun AlertItem(alert: AlertUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = alert.event,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = alert.headline,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun WeatherDetailContentPreview() {
    WeatherAppTheme(darkTheme = true) {
        WeatherDetailContent(
            uiModel = WeatherDetailUiModel(
                locationName = "New York",
                currentTemp = "22°",
                conditionText = "Partly Cloudy",
                conditionIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                highLowTemp = "25° / 15°",
                precipitation = "10%",
                wind = "15 km/h",
                windDir = "NE",
                uvIndex = "5 (Moderate)",
                sunTimes = "Sunrise: 6:30 AM\nSunset: 7:45 PM",
                hourlyForecast = emptyList(),
                dailyForecast = emptyList(),
                alerts = emptyList()
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun WeatherHeaderPreview() {
    WeatherAppTheme(darkTheme = true) {
        WeatherHeader(
            WeatherDetailUiModel(
                locationName = "New York",
                currentTemp = "22°",
                conditionText = "Partly Cloudy",
                conditionIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                highLowTemp = "25° / 15°",
                precipitation = "10%",
                wind = "15 km/h",
                windDir = "NE",
                uvIndex = "5 (Moderate)",
                sunTimes = "Sunrise: 6:30 AM\nSunset: 7:45 PM",
                hourlyForecast = emptyList(),
                dailyForecast = emptyList(),
                alerts = emptyList()
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun TelemetrySectionPreview() {
    WeatherAppTheme(darkTheme = true) {
        TelemetrySection(
            WeatherDetailUiModel(
                locationName = "New York",
                currentTemp = "22°",
                conditionText = "Partly Cloudy",
                conditionIcon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                highLowTemp = "25° / 15°",
                precipitation = "10%",
                wind = "15 km/h",
                windDir = "NE",
                uvIndex = "5 (Moderate)",
                sunTimes = "Sunrise: 6:30 AM\nSunset: 7:45 PM",
                hourlyForecast = emptyList(),
                dailyForecast = emptyList(),
                alerts = emptyList()
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun HourlySectionPreview() {
    WeatherAppTheme(darkTheme = true) {
        HourlySection(
            condition = "Partly Cloudy",
            hourly = listOf(
                HourlyForecastUiModel(
                    time = "2 PM",
                    temp = "22°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    timeEpoch = 0
                ),
                HourlyForecastUiModel(
                    time = "3 PM",
                    temp = "21°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    timeEpoch = 3600
                ),
                HourlyForecastUiModel(
                    time = "4 PM",
                    temp = "20°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                    timeEpoch = 7200
                )
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun FiveDayForecastSectionPreview() {
    WeatherAppTheme(darkTheme = true) {
        FiveDayForecastSection(
            forecast = listOf(
                DailyForecastUiModel(
                    day = "Monday",
                    date = "Oct 2",
                    highLowTemp = "15° / 7°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/116.png"
                ),
                DailyForecastUiModel(
                    day = "Tuesday",
                    date = "Oct 3",
                    highLowTemp = "17° / 9°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/113.png"
                ),
                DailyForecastUiModel(
                    day = "Wednesday",
                    date = "Oct 4",
                    highLowTemp = "14° / 6°",
                    icon = "https://cdn.weatherapi.com/weather/64x64/day/119.png"
                )
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
fun AlertsSectionPreview() {
    WeatherAppTheme(darkTheme = true) {
        AlertsSection(
            alerts = listOf(
                AlertUiModel(
                    event = "Severe Thunderstorm Warning",
                    headline = "A severe thunderstorm has been detected in your area. Seek shelter immediately and avoid outdoor activities until the storm passes."
                ),
                AlertUiModel(
                    event = "Flood Watch",
                    headline = "Heavy rainfall expected over the next 24 hours. Monitor local news and be prepared for potential flooding in low-lying areas."
                )
            )
        )
    }
}