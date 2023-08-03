package com.adsama.network

object WeatherApi {

    val weatherService: WeatherService by lazy {
        retrofitClient().create(WeatherService::class.java)
    }

}