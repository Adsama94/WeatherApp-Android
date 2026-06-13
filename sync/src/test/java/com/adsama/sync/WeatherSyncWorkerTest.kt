package com.adsama.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.adsama.domain.DispatcherProvider
import com.adsama.domain.WeatherDataSource
import com.adsama.domain.model.Result
import com.adsama.domain.model.WeatherLocation
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class WeatherSyncWorkerTest {

    private lateinit var context: Context
    private val weatherDataSource = mockk<WeatherDataSource>()
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `doWork should return success when locations are refreshed`() = runTest(testDispatcher) {
        // Given
        val locations = listOf(
            WeatherLocation(1, "London", "", "", 0.0, 0.0)
        )
        every { weatherDataSource.getAllSavedLocations() } returns flowOf(Result.Success(locations))
        coEvery { weatherDataSource.getForecast(any()) } returns Result.Success(mockk())

        val worker = TestListenableWorkerBuilder<WeatherSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return WeatherSyncWorker(
                        appContext,
                        workerParameters,
                        weatherDataSource,
                        testDispatcherProvider
                    )
                }
            })
            .build()

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify(exactly = 1) { weatherDataSource.getForecast("London") }
    }

    @Test
    fun `doWork should return retry when local data fetch fails`() = runTest(testDispatcher) {
        // Given
        every { weatherDataSource.getAllSavedLocations() } returns flowOf(Result.Error(mockk()))

        val worker = TestListenableWorkerBuilder<WeatherSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return WeatherSyncWorker(
                        appContext,
                        workerParameters,
                        weatherDataSource,
                        testDispatcherProvider
                    )
                }
            })
            .build()

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
