package com.adsama.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(getInterceptor()).build()

private fun getInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

fun retrofitClient(): Retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BuildConfig.BASE_URL)
    .build()