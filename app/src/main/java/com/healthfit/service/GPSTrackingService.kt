package com.healthfit.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.healthfit.MainActivity
import com.healthfit.R
import kotlinx.coroutines.*

/**
 * Foreground service that tracks the user's GPS route during a workout.
 * Uses Google's FusedLocationProviderClient for battery-efficient GPS.
 *
 * Sends broadcasts with live distance and speed so the UI can update in real time.
 * Only runs when the user explicitly starts an activity — does NOT run in background.
 */
class GPSTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastLocation: Location? = null
    private var totalDistanceMeters = 0.0
    private var startTimeMs = 0L
    private var currentSpeedMs = 0f

    companion object {
        const val CHANNEL_ID = "gps_tracking_channel"
        const val NOTIFICATION_ID = 1002

        // Broadcast actions
        const val ACTION_LOCATION_UPDATE = "com.healthfit.GPS_UPDATE"
        const val EXTRA_DISTANCE_KM = "distance_km"
        const val EXTRA_SPEED_KMH = "speed_kmh"
        const val EXTRA_DURATION_SEC = "duration_sec"
        const val EXTRA_CALORIES = "calories"

        // GPS update interval — 3 seconds balances accuracy vs battery
        private const val UPDATE_INTERVAL_MS = 3000L
        private const val FASTEST_INTERVAL_MS = 1500L

        fun startTracking(context: Context) {
            val intent = Intent(context, GPSTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopTracking(context: Context) {
            context.stopService(Intent(context, GPSTrackingService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startTimeMs = System.currentTimeMillis()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(0.0, 0f))
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            UPDATE_INTERVAL_MS
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            setMinUpdateDistanceMeters(5f)   // only update if moved 5+ metres
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return

                // Calculate distance from last point
                lastLocation?.let { prev ->
                    val distanceDelta = prev.distanceTo(location)
                    // Filter GPS noise: ignore jumps > 100 m/s (~360 km/h)
                    if (distanceDelta / ((location.time - prev.time) / 1000.0) < 100) {
                        totalDistanceMeters += distanceDelta
                    }
                }

                currentSpeedMs = location.speed
                lastLocation = location

                val distanceKm = totalDistanceMeters / 1000.0
                val speedKmh = currentSpeedMs * 3.6f
                val durationSec = (System.currentTimeMillis() - startTimeMs) / 1000
                // ~0.75 kcal per km per kg (using 70kg reference weight)
                val calories = (totalDistanceMeters / 1000.0 * 52.5).toFloat()

                // Broadcast to UI
                val intent = Intent(ACTION_LOCATION_UPDATE).apply {
                    putExtra(EXTRA_DISTANCE_KM, distanceKm)
                    putExtra(EXTRA_SPEED_KMH, speedKmh)
                    putExtra(EXTRA_DURATION_SEC, durationSec)
                    putExtra(EXTRA_CALORIES, calories)
                }
                sendBroadcast(intent)
                updateNotification(distanceKm, speedKmh)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission was revoked while service was running
            stopSelf()
        }
    }

    // Returns a summary of the completed workout for saving
    fun getWorkoutSummary(): WorkoutSummary {
        val durationMin = ((System.currentTimeMillis() - startTimeMs) / 60000).toInt()
        val distanceKm = totalDistanceMeters / 1000.0
        val calories = (distanceKm * 52.5).toFloat()
        return WorkoutSummary(durationMin, distanceKm.toFloat(), calories)
    }

    data class WorkoutSummary(
        val durationMinutes: Int,
        val distanceKm: Float,
        val caloriesBurned: Float
    )

    private fun buildNotification(distanceKm: Double, speedKmh: Float): Notification {
        val pending = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val distText = String.format("%.2f km", distanceKm)
        val speedText = String.format("%.1f km/h", speedKmh)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HealthFit — GPS Active")
            .setContentText("$distText · $speedText")
            .setSmallIcon(R.drawable.ic_gps_notification)
            .setContentIntent(pending)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(distanceKm: Double, speedKmh: Float) {
        val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(NOTIFICATION_ID, buildNotification(distanceKm, speedKmh))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "GPS Workout Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Live GPS during workouts"
                setShowBadge(false)
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(ch)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        scope.cancel()
        super.onDestroy()
    }
}
