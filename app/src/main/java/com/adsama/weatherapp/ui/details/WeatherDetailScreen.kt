package com.adsama.weatherapp.ui.details

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adsama.model.Alert
import com.adsama.model.ForecastDay
import com.adsama.model.ForecastResponse
import com.adsama.model.Hour
import com.adsama.weatherapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    locationName: String,
    viewModel: WeatherDetailViewModel,
    onBack: () -> Unit,
) {
    val forecast by viewModel.forecastResponse.collectAsStateWithLifecycle()
    val hourly by viewModel.hourlyResponse.collectAsStateWithLifecycle()
    val fiveDayForecast by viewModel.fiveDayForecastResponse.collectAsStateWithLifecycle()
    val alerts by viewModel.alertsResponse.collectAsStateWithLifecycle()
    val isPersisted by viewModel.isPersisted.collectAsStateWithLifecycle()
    val isLoading by viewModel.showProgressBar.collectAsStateWithLifecycle()

    LaunchedEffect(locationName) {
        viewModel.getForecastData(locationName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = forecast?.location?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (forecast != null) {
                        IconButton(onClick = {
                            if (isPersisted) viewModel.removeLocationFromSaved()
                            else viewModel.saveLocationData()
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (isPersisted) R.drawable.bookmark_remove
                                    else R.drawable.bookmark_add
                                ),
                                contentDescription = if (isPersisted) "Saved" else "Save"
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
            if (isLoading && forecast == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                forecast?.let { data ->
                    WeatherDetailContent(
                        data = data,
                        hourly = hourly,
                        fiveDayForecast = fiveDayForecast,
                        alerts = alerts
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailContent(
    data: ForecastResponse,
    hourly: List<Hour>,
    fiveDayForecast: List<ForecastDay>,
    alerts: List<Alert>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WeatherHeader(data)
        }
        item {
            TelemetrySection(data)
        }
        item {
            HourlySection(data.current.condition.text, hourly)
        }
        item {
            FiveDayForecastSection(fiveDayForecast)
        }
        if (alerts.isNotEmpty()) {
            item {
                AlertsSection(alerts)
            }
        }
    }
}

@Composable
fun WeatherHeader(data: ForecastResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp), contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.cloud_sun_rain),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified
            )
        }
        Text(
            text = "${data.current.temp_c.toInt()}°",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            color = Color.Gray
        )
        Text(
            text = "H:${data.forecast.forecastday[0].day.maxtemp_c.toInt()}°  L:${data.forecast.forecastday[0].day.mintemp_c.toInt()}°",
            fontSize = 16.sp
        )
    }
}

@Composable
fun TelemetrySection(data: ForecastResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.rainy,
                label = stringResource(R.string.precipitation),
                value = "${data.current.precip_mm} mm"
            )
            TelemetryCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.air,
                label = stringResource(R.string.wind),
                value = "${data.current.wind_kph} kph",
                extra = data.current.wind_dir
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TelemetryCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.outline_wb_sunny,
                label = stringResource(R.string.uv_index),
                value = data.current.uv.toString()
            )
            TelemetryCard(
                modifier = Modifier.weight(1f),
                icon = R.drawable.sunny,
                label = stringResource(R.string.sun),
                value = "↑ ${data.forecast.forecastday[0].astro.sunrise}\n↓ ${data.forecast.forecastday[0].astro.sunset}"
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF364A7D)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 12.sp, color = Color(0xFF364A7D))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (extra != null) {
                    Text(text = extra, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun HourlySection(condition: String, hourly: List<Hour>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = condition,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 14.sp
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFEFEFEF)
            )
            val currentTimeEpoch = System.currentTimeMillis() / 1000
            val filteredHourly = hourly.filter { it.time_epoch >= currentTimeEpoch }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredHourly) { hour ->
                    HourlyItem(hour)
                }
            }
        }
    }
}

@Composable
fun HourlyItem(hour: Hour) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = hour.time.split(" ")[1], fontSize = 12.sp)
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(
                painterResource(R.drawable.cloud_sun_rain),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified
            )
        }
        Text(text = "${hour.temp_c.toInt()}°", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FiveDayForecastSection(forecast: List<ForecastDay>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.calendar_forecast),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF364A7D)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.five_day_forecast),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF364A7D)
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
fun ForecastDayItem(day: ForecastDay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = day.date, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(
                painterResource(R.drawable.cloud_sun_rain),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified
            )
        }
        Text(
            text = "${day.day.maxtemp_c.toInt()}° / ${day.day.mintemp_c.toInt()}°",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun AlertsSection(alerts: List<Alert>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.notifications),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF364A7D)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.alerts),
                fontSize = 14.sp,
                color = Color(0xFF364A7D)
            )
        }
        alerts.forEach { alert ->
            AlertItem(alert)
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = alert.event ?: "Alert", fontWeight = FontWeight.Bold, color = Color.Red)
            Text(text = alert.headline ?: "", fontSize = 12.sp)
        }
    }
}