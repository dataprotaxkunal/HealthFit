package com.healthfit.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.healthfit.MainActivity
import com.healthfit.R
import com.healthfit.data.AppDatabase
import com.healthfit.data.entities.DailySteps
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Foreground service that listens to the hardware step counter sensor.
 * TYPE_STEP_COUNTER gives cumulative steps since last reboot — we store a
 * baseline at midnight and subtract to get today's steps.
 */
class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var database: AppDatabase
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        const val CHANNEL_ID = "step_counter_channel"
        const val NOTIFICATION_ID = 1001
        const val PREF_NAME = "step_prefs"
        const val PREF_BASELINE = "step_baseline"
        const val PREF_BASELINE_DATE = "step_baseline_date"
        const val PREF_LAST_DATE = "step_last_date"

        fun startService(context: Context) {
            val intent = Intent(context, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, StepCounterService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        database = AppDatabase.getDatabase(applicationContext)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(0))

        // Register the step sensor listener
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        val totalStepsSinceReboot = event.values[0].toLong()
        val today = dateFormat.format(Date())
        val lastDate = prefs.getString(PREF_LAST_DATE, "")
        val baselineDate = prefs.getString(PREF_BASELINE_DATE, "")

        // New day: reset baseline
        if (today != lastDate) {
            if (baselineDate != today) {
                // Save new baseline at the start of today
                prefs.edit()
                    .putLong(PREF_BASELINE, totalStepsSinceReboot)
                    .putString(PREF_BASELINE_DATE, today)
                    .putString(PREF_LAST_DATE, today)
                    .apply()
            }
        }

        val baseline = prefs.getLong(PREF_BASELINE, totalStepsSinceReboot)
        val todaySteps = (totalStepsSinceReboot - baseline).coerceAtLeast(0).toInt()

        // Update notification
        updateNotification(todaySteps)

        // Persist to database
        val calories = todaySteps * 0.04f        // ~0.04 kcal per step
        val distanceKm = todaySteps * 0.000762f  // avg stride 76.2 cm

        scope.launch {
            database.physicalActivityDao().insertOrUpdateDailySteps(
                DailySteps(
                    date = today,
                    steps = todaySteps,
                    goal = 10000,
                    caloriesBurned = calories,
                    distanceKm = distanceKm
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { /* not needed */ }

    private fun buildNotification(steps: Int): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HealthFit — Step Tracker")
            .setContentText("Today: $steps steps")
            .setSmallIcon(R.drawable.ic_steps_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(steps))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Live step count notification"
                setShowBadge(false)
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        scope.cancel()
        super.onDestroy()
    }
}
