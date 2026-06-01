package com.adsama.domain

interface TimeProvider {
    fun getCurrentTimeMillis(): Long
}
