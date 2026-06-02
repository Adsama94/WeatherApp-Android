package com.adsama.network.interceptor

import com.adsama.network.BuildConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiKeyInterceptorTest {

    @Test
    fun `intercept should add API key to query parameters`() {
        val interceptor = ApiKeyInterceptor()
        val chain = mockk<Interceptor.Chain>()
        val originalRequest = Request.Builder()
            .url("https://api.weatherapi.com/v1/forecast.json?q=London")
            .build()
        
        val requestSlot = slot<Request>()
        
        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(requestSlot)) } returns mockk()

        interceptor.intercept(chain)

        val interceptedRequest = requestSlot.captured
        val url = interceptedRequest.url
        assertEquals(BuildConfig.API_KEY, url.queryParameter("key"))
        assertEquals("London", url.queryParameter("q"))
    }
}
