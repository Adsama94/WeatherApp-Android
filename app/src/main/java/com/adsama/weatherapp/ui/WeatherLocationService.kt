package com.adsama.weatherapp.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adsama.weatherapp.R
import com.adsama.weatherapp.utils.LocationCallbacks
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherLocationService : Service() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallbacks: LocationCallbacks? = null

    companion object {
        const val ACTION_GET_LOCATION_ONCE = "ACTION_GET_LOCATION_ONCE"
        private const val CHANNEL_ID = "WeatherAppId911"
        private const val NOTIF_ID = 911
    }

    inner class LocalBinder : Binder() {
        fun getService(): WeatherLocationService = this@WeatherLocationService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_GET_LOCATION_ONCE) {
            // In Android 12+, startForeground must be called within 10 seconds of startForegroundService()
            startServiceForeground()
            getLocationOnce()
        }
        return START_NOT_STICKY
    }

    private fun startServiceForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Update")
            .setContentText("Fetching your current location...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

        // Added Foreground Service Type for API 34+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIF_ID, notification)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationOnce() {
        // Optimized for single-shot location
        val priority = Priority.PRIORITY_HIGH_ACCURACY

        fusedLocationClient?.getCurrentLocation(priority, null)
            ?.addOnSuccessListener { location ->
                location?.let {
                    mLocationCallbacks?.getLocation(it.latitude, it.longitude)
                }
                cleanupAndStop()
            }
            ?.addOnFailureListener {
                cleanupAndStop()
            }
    }

    private fun cleanupAndStop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun setCallback(callback: LocationCallbacks?) {
        this.mLocationCallbacks = callback
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather App Location Channel",
                NotificationManager.IMPORTANCE_LOW // Lower importance as it's a brief task
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}