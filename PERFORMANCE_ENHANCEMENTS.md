# Performance Enhancements & Testing Plan

This document outlines the strategy for improving the WeatherApp-Android performance, reliability, and maintainability through structured testing and optimized data fetching.

## 1. Testing Plan

### 1.1 Unit Testing (JUnit 5 + MockK)

#### **Domain Layer**
*   **Goal:** Ensure business logic is correct and independent of infrastructure.
*   **Test Cases:**
    *   `SearchLocationUseCase`: Validate empty queries (return `ValidationError`), successful search (map to `Result.Success`), and network errors.
    *   `FetchCurrentWeatherUseCase`: Verify `forceRefresh` logic and error propagation.
    *   `SaveLocationUseCase`/`DeleteLocationUseCase`: Ensure repository calls are made with correct parameters.

#### **Data Layer**
*   **Goal:** Verify data orchestration logic (Local vs. Remote).
*   **Test Cases:**
    *   `WeatherDataRepository`:
        *   Return local data if `forceRefresh` is false and cache exists.
        *   Trigger remote fetch if `forceRefresh` is true or cache is missing.
        *   Ensure remote success updates the local database.
        *   Handle remote failure by falling back to cache if available.

#### **ViewModel Layer**
*   **Goal:** Ensure UI state reflects data layer results and user interactions.
*   **Test Cases:**
    *   `WeatherHomeViewModel`:
        *   Initialization triggers `observeSavedLocations`.
        *   `searchLocation` updates `searchSuggestions` or `error` state.
        *   `refreshWeatherForLocation` correctly updates `refreshingLocationIds`.
    *   `WeatherDetailViewModel`:
        *   Loading weather report updates state from `Loading` to `Success`/`Error`.
        *   Toggling bookmark calls correct use cases.

### 1.2 UI Testing (Compose Test Rule + Hilt)

#### **Home Screen**
*   **Test Cases:**
    *   `search_showsSuggestions`: Typing in search bar displays the results list.
    *   `savedLocations_areDisplayed`: Verify that locations from the database appear as cards.
    *   `swipeToRefresh_triggersUpdate`: (Post-implementation) Verify that swiping triggers `refreshWeatherForLocation` in the ViewModel.
    *   `emptyState_isShown`: Verify empty hint when no locations are saved.

#### **Detail Screen**
*   **Test Cases:**
    *   `weatherDetails_loadCorrectData`: Verify temperature and forecast display for a specific location.
    *   `bookmarkToggle_updatesUi`: Verify icon change when bookmarking/unbookmarking.

---

## 2. Swipe-to-Refresh Implementation

### **Objective**
Allow users to manually trigger a refresh for all saved locations on the Home Screen.

### **Technical Strategy**
1.  **UI:** Wrap the `LazyColumn` in `WeatherHomeScreen.kt` with Material3's `PullToRefreshBox`.
2.  **ViewModel:** Add a `refreshAllLocations()` function that iterates through `savedLocations` and calls `refreshWeatherForLocation(location, forceRefresh = true)`.
3.  **State:** Use a combined loading state to show the refresh indicator until all locations have completed their fetch.

---

## 3. Idempotency & Data Fetching Optimization

### **Problem**
App restarts and manual refreshes may exhaust API limits by fetching data that was recently updated.

### **Strategy for Idempotency**
1.  **Staleness Check (Repository Level):**
    *   Modify `WeatherDataRepository` to check the `lastUpdated` timestamp.
    *   Even if `forceRefresh = true`, do NOT fetch from remote if the data is less than **15 minutes** old.
2.  **Session-based Tracking (ViewModel Level):**
    *   Maintain the `refreshedLocationIds` set to prevent redundant fetches during the same app session unless manually triggered.
3.  **Background Refresh (Optional):**
    *   Limit automatic refreshes on app resume to once every hour.

### **Implementation Details**
*   Use `System.currentTimeMillis()` or ISO 8601 timestamps in the `date` field of `PersistedWeatherModel`.
*   Introduce a constant `STALE_THRESHOLD_MS = 15 * 60 * 1000`.
