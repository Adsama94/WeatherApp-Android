package com.adsama.domain

interface SyncManager {
    fun schedulePeriodicSync()
    fun cancelSync()
}
