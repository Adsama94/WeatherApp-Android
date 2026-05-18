# Pending Test Cases for WeatherApp-Android

## 1. UseCase Tests (Domain Layer)

### `SearchLocationUseCaseTest`
* **Case 1: Empty Query** - Should return a `ValidationError` immediately without calling the repository.
* **Case 2: Successful Search** - Should return `Result.Success` with a list of matching locations when the repository succeeds.
* **Case 3: Network Error** - Should propagate the `Result.Error` from the repository (e.g., connection failure).

### `SaveLocationUseCaseTest`
* **Case 1: Successful Save** - Should verify that the repository's `saveLocation` is called with the correct parameters and returns `Success(Unit)`.
* **Case 2: Database Failure** - Should propagate the error if the database write fails.

---

## 2. ViewModel Tests (App Layer)

### `WeatherHomeViewModelTest`
* **Case 1: Initialization** - Should verify that `getAllSavedLocations()` is called on init and UI state reflects the saved data.
* **Case 2: Search Flow** - 
    * Entering text triggers `searchLocationUseCase`.
    * Success updates `searchSuggestions` in `HomeUiState`.
    * Error updates the `error` field in `HomeUiState`.
* **Case 3: Refresh Logic** -
    * Triggering refresh updates `refreshingLocationIds`.
    * Successful fetch updates `freshWeatherData` and persists the new data.
* **Case 4: Remove Location** -
    * Deleting a location calls `deleteLocationUseCase`.
    * On success, the list is re-fetched or updated locally.

### `WeatherDetailViewModelTest`
* **Case 1: Load Forecast** - Should verify `fetchCurrentWeatherUseCase` is called with the location name and UI state updates to `Loading` then `Success`.
* **Case 2: Toggle Bookmark** -
    * If location is NOT saved, clicking bookmark calls `saveLocationUseCase`.
    * If location IS saved, clicking bookmark calls `deleteLocationUseCase`.
* **Case 3: Error Handling** - Verify that API errors (e.g., 404 or rate limit) are correctly mapped to UI-readable errors.

---

## 3. Data Layer Tests

### `RemoteWeatherDataSourceImplTest`
* **Case 1: Successful API Mapping** - Verify that a `NetworkResponse.Success` from Retrofit is correctly transformed into a `Result.Success<WeatherReport>` in the domain.
* **Case 2: API Error Mapping** - Verify that `NetworkResponse.ApiError` (e.g., invalid key) maps to `DomainError.ApiError`.
* **Case 3: IOException Mapping** - Verify that network connectivity issues map to `DomainError.NetworkError`.
