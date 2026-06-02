package com.adsama.network

import com.adsama.network.adapter.NetworkResponse
import com.adsama.network.adapter.NetworkResponseAdapterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class WeatherServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: WeatherService

    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val converterFactory = json.asConverterFactory("application/json".toMediaType())
        
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
            .create(WeatherService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getSearchResults should parse successful response correctly`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                  {
                    "name": "London",
                    "region": "Greater London",
                    "country": "United Kingdom",
                    "lat": 51.52,
                    "lon": -0.11
                  }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = service.getSearchResults("London")

        assertTrue(response is NetworkResponse.Success)
        val body = (response as NetworkResponse.Success).body
        assertEquals(1, body.size)
        assertEquals("London", body[0].name)
        
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/search.json?q=London", recordedRequest.path)
    }

    @Test
    fun `getForecast should parse API error correctly`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""
                {
                  "error": {
                    "code": 1006,
                    "message": "No location found matching parameter 'q'"
                  }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = service.getForecast("InvalidCity")

        assertTrue(response is NetworkResponse.ApiError)
        val body = (response as NetworkResponse.ApiError).body
        assertEquals(1006, body.error.code)
    }
}
