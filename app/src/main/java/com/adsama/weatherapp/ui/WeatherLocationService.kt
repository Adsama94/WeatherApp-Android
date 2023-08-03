package com.adsama.weatherapp.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.adsama.weatherapp.R
import com.adsama.weatherapp.utils.LocationCallbacks
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallbacks: LocationCallbacks

    companion object {
        const val ACTION_GET_LOCATION_ONCE = "ACTION_GET_LOCATION_ONCE"
    }

    inner class LocalBinder : Binder() {
        fun getService(): WeatherLocationService = this@WeatherLocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        showNotification()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "WeatherAppId911",
                "Weather App Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, "WeatherAppId911")
            .setContentTitle("Weather Foreground Service")
            .setContentText("Getting approximate location")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(911, notification)
    }

    fun setCallback(callback: LocationCallbacks) {
        mLocationCallbacks = callback
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == ACTION_GET_LOCATION_ONCE) {
                getLocationOnce()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun getLocationOnce() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.Builder(1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdates(1)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val lat = location.latitude
                    val long = location.longitude
                    mLocationCallbacks.getLocation(lat, long)
                    stopSelf()
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

}