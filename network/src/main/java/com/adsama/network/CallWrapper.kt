package com.adsama.network

import android.util.Log
import com.adsama.model.Error
import com.adsama.model.WeatherErrorResponse
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): ResponseWrapper<Response<T>> {
    return try {
        val response = apiCall()
        val responseCode = response.code()
        if (response.isSuccessful && (responseCode in 200..299))
            ResponseWrapper.Success(response)
        else {
            ResponseWrapper.NetworkError(WeatherErrorResponse(Error(0, "")))
        }
    } catch (t: Throwable) {
        when (t) {
            is IOException -> ResponseWrapper.Failure
            is HttpException -> {
                val code = t.code()
                var errorResponse = convertErrorBody(t)
                if (errorResponse == null)
                    errorResponse =
                        WeatherErrorResponse(Error(code, ""))
                ResponseWrapper.NetworkError(errorResponse)
            }

            else -> {
                Log.e("CallWrapper ", "Message is " + t.message)
                ResponseWrapper.NetworkError(WeatherErrorResponse(Error(1, t.message)))
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): WeatherErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val moshiAdapter = Moshi.Builder().build().adapter(WeatherErrorResponse::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
        return null
    }
}
