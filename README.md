# WeatherApp-Android
Simple Weather data fetching Android app

A simple Android App built using Kotlin with MVVM + Clean Architecture with DI.

A WeatherAPI key is required and can be found at https://www.weatherapi.com.
The key needs to be stored in the local gradle.properties file.

Libraries used - 

Jetpack Components(LiveData, Databinding, NavigationFragment, NavigationUI, RoomDB)
Kotlin Coroutines
Retrofit
Glide
Dagger Hilt

The architecture flow followed is - UI(Fragment/Activity) -> ViewModel -> UseCase -> Source -> Repo.

Some kinks still remain and are being worked on in other branches.
