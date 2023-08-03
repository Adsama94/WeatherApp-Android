package com.adsama.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResponse(
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
    val country: String
) : Parcelable