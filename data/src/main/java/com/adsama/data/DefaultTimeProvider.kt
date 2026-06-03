package com.adsama.data

import com.adsama.domain.TimeProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTimeProvider @Inject constructor() : TimeProvider {
    override fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}
