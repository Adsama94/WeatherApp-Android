package com.adsama.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            // 1. Interact with the search bar
            device.wait(Until.hasObject(By.res("search_bar")), 5000)
            val searchBar = device.findObject(By.res("search_bar"))
            searchBar?.click()
            
            // Type a common location (find the input field inside the search bar)
            // For Material3 SearchBar, the input field text can be set directly on the SearchBar object in UI Automator sometimes,
            // or we find the focused element.
            device.waitForIdle()
            device.findObject(By.focused(true))?.text = "London"
            device.waitForIdle()

            // 2. Wait for suggestions and click one
            device.wait(Until.hasObject(By.textContains("London")), 5000)
            val suggestion = device.findObject(By.textContains("London"))
            suggestion?.click()

            // 3. We should now be on the detail screen. Wait for weather data to load.
            val detailHeader = "5-day forecast"
            device.wait(Until.hasObject(By.text(detailHeader)), 5000)
            
            // 4. Scroll through the detail screen
            val scrollable = device.findObject(By.scrollable(true))
            scrollable?.setGestureMargin(device.displayWidth / 4)
            scrollable?.fling(Direction.DOWN)
            device.waitForIdle()

            // 5. Go back to home
            val backButton = device.findObject(By.res("back_button"))
            backButton?.click()
            device.waitForIdle()
            
            // 6. Scroll the home screen
            device.wait(Until.hasObject(By.res("search_bar")), 5000)
            val savedLocationsList = device.findObject(By.res("saved_locations_list"))
            savedLocationsList?.fling(Direction.DOWN)
            device.waitForIdle()
        }
    }
}