# WeatherApp-Android

A modern, clean, and feature-rich Android Weather application built with Kotlin and Jetpack Compose.
This project follows **Clean Architecture** principles and is structured into multiple modules to
ensure scalability, testability, and maintainability.

## 🚀 Features

- **Real-time Weather**: Get current weather, hourly forecast, and 5-day daily forecast for any
  location.
- **Smart Search**: Autocomplete search suggestions to quickly find cities across the globe.
- **Current Location**: Automatic weather detection based on your device's GPS coordinates.
- **Persistent Favorites**: Save your favorite locations for quick access, with offline caching
  support.
- **Smart Refresh**: Optimized "Smart Pull-to-Refresh" that respects a 5-minute threshold to reduce
  unnecessary API calls while keeping data fresh.
- **Weather Alerts**: Stay informed with severe weather alerts (when available from the API).
- **Modern UI**: Fully built with Jetpack Compose and Material 3, supporting both Dark and Light
  themes.
- **Robust Parsing**: Handles missing API data gracefully with optional fields and default values.

## 🏗️ Architecture

The project is designed using **Clean Architecture** and **MVVM** patterns, separated into clear
layers through a multi-module setup:

- **`:app`**: The UI layer containing Jetpack Compose screens, ViewModels, and Dependency Injection
  setup (Hilt).
- **`:domain`**: The core business logic containing UseCases, Domain Models, and Repository
  interfaces. This module has no dependencies on Android or external libraries.
- **`:data`**: The orchestration layer that implements repositories and coordinates between local
  and remote data sources.
- **`:network`**: Remote data source implementation using Retrofit and Kotlinx Serialization.
- **`:database`**: Local persistence layer using Room Database.
- **`:location`**: Device-specific location services using FusedLocationProvider.
- **`:model`**: Shared data transfer objects (DTOs) and models used across modules.

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking
  **: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Async & Flow
  **: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Architecture Components**: ViewModel, Navigation Compose, StateFlow

## 🚦 Getting Started

### Prerequisites

- Android Studio Koala (or newer)
- WeatherAPI Key (Get it for free at [weatherapi.com](https://www.weatherapi.com))

### Setup

1. Clone the repository.
2. Open the project in Android Studio.
3. Obtain an API key from [WeatherAPI](https://www.weatherapi.com).
4. Create or update your `local.properties` file in the root directory and add your API key:
   ```properties
   API_KEY=your_api_key_here
   ```
5. Build and run the app.

---
*Developed as a showcase for modern Android development practices.*
