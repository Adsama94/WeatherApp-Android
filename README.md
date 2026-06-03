# WeatherApp-Android

A modern, clean, and feature-rich Android Weather application built with Kotlin and Jetpack Compose.
This project follows **Clean Architecture** principles and is structured into multiple modules to
ensure scalability, high testability, and long-term maintainability.

## 🚀 Features

- **Real-time Weather**: Get current weather, hourly forecast, and 5-day daily forecast for any
  location.
- **Smart Search**: Autocomplete search suggestions to quickly find cities across the globe.
- **Current Location**: Automatic weather detection based on your device's GPS coordinates.
- **Persistent Favorites**: Save your favorite locations for quick access, with offline caching
  support.
- **Smart Refresh**: Optimized "Smart Pull-to-Refresh" that respects a 5-minute threshold to reduce
  unnecessary API calls while keeping data fresh.
- **Modern UI**: Fully built with Jetpack Compose and Material 3, supporting both Dark and Light
  themes.
- **Deterministic Architecture**: Injected Dispatcher and Time providers to ensure predictable
  behavior and 100% stable testing.

## 🏆 Quality & Testing

This project is built with a **test-first mindset**, achieving a comprehensive **80%+ line coverage**
milestone across all architectural layers.

- **Unit Tests**: Full coverage for ViewModels, UseCases, Mappers, and Repositories using **MockK**.
- **Flow Testing**: Complex asynchronous streams verified using **Turbine**.
- **Integration Tests**: Network layer verified against a local **MockWebServer**.
- **UI Tests**: Robust instrumented tests for Compose screens and components on real devices/emulators.
- **Database Tests**: Instrumented Room DAO tests using in-memory databases.

## 🏗️ Architecture

The project is designed using **Clean Architecture** and **MVVM** patterns, separated into clear
layers through a multi-module setup:

- **`:app`**: The UI layer containing Jetpack Compose screens, ViewModels, and Dependency Injection setup.
- **`:domain`**: The core business logic containing UseCases, Domain Models, and Repository interfaces. Includes `DispatcherProvider` and `TimeProvider` abstractions for deterministic execution.
- **`:data`**: The orchestration layer that implements repositories and coordinates between local and remote data sources.
- **`:network`**: Remote data source implementation using Retrofit, featuring custom call adapters for robust error handling.
- **`:database`**: Local persistence layer using Room Database and TypeConverters.
- **`:location`**: Device-specific location services using FusedLocationProvider.
- **`:model`**: Shared data transfer objects (DTOs) used for API communication.

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Testing**: [MockK](https://mockk.io/), [Turbine](https://github.com/cashapp/turbine), [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
- **Coverage**: [Kotlin Kover](https://github.com/Kotlin/kotlinx-kover)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Async & Flow**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

## 🚦 Getting Started

### Setup

1. Clone the repository.
2. Obtain an API key from [WeatherAPI](https://www.weatherapi.com).
3. Add your API key to `local.properties`:
   ```properties
   API_KEY=your_api_key_here
   ```

### Running Tests

To run the full suite of unit tests and generate a coverage report:
```bash
./gradlew test koverHtmlReport
```
The report will be available at `build/reports/kover/html/index.html`.

To run instrumented UI and Database tests (requires a connected device/emulator):
```bash
./gradlew connectedDebugAndroidTest
```

---
*Developed as a showcase for professional Android engineering standards.*
