package com.adsama.network.adapter

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class NetworkResponseCallTest {

    private val delegate = mockk<Call<String>>()
    private val errorConverter = mockk<Converter<ResponseBody, String>>()
    private val networkResponseCall = NetworkResponseCall(delegate, errorConverter)

    @Test
    fun `enqueue returns success when delegate returns successful response`() {
        val callback = mockk<Callback<NetworkResponse<String, String>>>(relaxed = true)
        val responseSlot = slot<Response<NetworkResponse<String, String>>>()
        val delegateCallbackSlot = slot<Callback<String>>()

        every { delegate.enqueue(capture(delegateCallbackSlot)) } answers {
            delegateCallbackSlot.captured.onResponse(delegate, Response.success("Success Body"))
        }

        networkResponseCall.enqueue(callback)

        io.mockk.verify { callback.onResponse(any(), capture(responseSlot)) }
        assertTrue(responseSlot.captured.body() is NetworkResponse.Success)
    }

    @Test
    fun `enqueue returns ApiError when delegate returns unsuccessful response`() {
        val callback = mockk<Callback<NetworkResponse<String, String>>>(relaxed = true)
        val responseSlot = slot<Response<NetworkResponse<String, String>>>()
        val delegateCallbackSlot = slot<Callback<String>>()
        val errorBody = "Error Body".toResponseBody(null)

        every { delegate.enqueue(capture(delegateCallbackSlot)) } answers {
            delegateCallbackSlot.captured.onResponse(delegate, Response.error(404, errorBody))
        }
        every { errorConverter.convert(any()) } returns "Mapped Error"

        networkResponseCall.enqueue(callback)

        io.mockk.verify { callback.onResponse(any(), capture(responseSlot)) }
        assertTrue(responseSlot.captured.body() is NetworkResponse.ApiError)
    }

    @Test
    fun `enqueue returns NetworkError when delegate fails with IOException`() {
        val callback = mockk<Callback<NetworkResponse<String, String>>>(relaxed = true)
        val responseSlot = slot<Response<NetworkResponse<String, String>>>()
        val delegateCallbackSlot = slot<Callback<String>>()
        val exception = IOException("Network Failure")

        every { delegate.enqueue(capture(delegateCallbackSlot)) } answers {
            delegateCallbackSlot.captured.onFailure(delegate, exception)
        }

        networkResponseCall.enqueue(callback)

        io.mockk.verify { callback.onResponse(any(), capture(responseSlot)) }
        assertTrue(responseSlot.captured.body() is NetworkResponse.NetworkError)
    }
}
