package com.healthfit.ui.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.healthfit.data.entities.ActivityType
import com.healthfit.databinding.ActivityLiveWorkoutBinding
import com.healthfit.service.GPSTrackingService
import com.healthfit.util.PermissionHelper
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

/**
 * Full-screen workout screen shown while a GPS activity is in progress.
 * Shows live: elapsed time, distance, speed, calories.
 * On Stop → saves the workout to the database.
 */
class LiveWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveWorkoutBinding
    private lateinit var viewModel: HealthViewModel

    private var activityType = ActivityType.RUNNING
    private var isTracking = false
    private var elapsedSeconds = 0L
    private var distanceKm = 0.0
    private var speedKmh = 0f
    private var caloriesBurned = 0f

    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isTracking) {
                elapsedSeconds++
                updateTimerDisplay()
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    // Receives live location broadcasts from GPSTrackingService
    private val gpsReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != GPSTrackingService.ACTION_LOCATION_UPDATE) return
            distanceKm = intent.getDoubleExtra(GPSTrackingService.EXTRA_DISTANCE_KM, 0.0)
            speedKmh = intent.getFloatExtra(GPSTrackingService.EXTRA_SPEED_KMH, 0f)
            caloriesBurned = intent.getFloatExtra(GPSTrackingService.EXTRA_CALORIES, 0f)

            binding.tvDistance.text = String.format("%.2f", distanceKm)
            binding.tvSpeed.text = String.format("%.1f km/h", speedKmh)
            binding.tvCalories.text = "${caloriesBurned.toInt()} kcal"

            // Estimate pace (min/km)
            if (speedKmh > 0.5f) {
                val paceSecPerKm = (3600 / speedKmh).toInt()
                val paceMin = paceSecPerKm / 60
                val paceSec = paceSecPerKm % 60
                binding.tvPace.text = String.format("%d'%02d\"/km", paceMin, paceSec)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityType = intent.getStringExtra("activity_type")
            ?.let { ActivityType.valueOf(it) } ?: ActivityType.RUNNING

        viewModel = ViewModelProvider(
            this,
            HealthViewModelFactory(application)
        )[HealthViewModel::class.java]

        supportActionBar?.apply {
            title = activityType.name.replace("_", " ")
            setDisplayHomeAsUpEnabled(true)
        }

        binding.tvActivityEmoji.text = when (activityType) {
            ActivityType.RUNNING  -> "🏃"
            ActivityType.WALKING  -> "🚶"
            ActivityType.CYCLING  -> "🚴"
            ActivityType.SWIMMING -> "🏊"
            else -> "⚡"
        }

        binding.btnStartStop.setOnClickListener {
            if (isTracking) stopWorkout() else startWorkout()
        }
    }

    private fun startWorkout() {
        if (!PermissionHelper.hasLocationPermission(this)) {
            Toast.makeText(this, "Location permission required for GPS tracking", Toast.LENGTH_LONG).show()
            return
        }
        isTracking = true
        binding.btnStartStop.text = "⏹ Stop Workout"
        binding.btnStartStop.setBackgroundColor(0xFFE53935.toInt())
        GPSTrackingService.startTracking(this)
        timerHandler.post(timerRunnable)
    }

    private fun stopWorkout() {
        isTracking = false
        timerHandler.removeCallbacks(timerRunnable)
        GPSTrackingService.stopTracking(this)
        binding.btnStartStop.text = "▶ Start Workout"

        // Save to database
        val durationMin = (elapsedSeconds / 60).toInt().coerceAtLeast(1)
        val steps = estimateSteps(activityType, durationMin, distanceKm.toFloat())

        viewModel.insertActivity(
            type = activityType,
            durationMins = durationMin,
            calories = caloriesBurned,
            distance = distanceKm.toFloat(),
            steps = steps
        )

        Toast.makeText(this, "Workout saved! Great job! 💪", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun estimateSteps(type: ActivityType, durationMin: Int, distKm: Float): Int {
        return when (type) {
            ActivityType.WALKING  -> (distKm * 1312).toInt() // ~1312 steps/km walking
            ActivityType.RUNNING  -> (distKm * 1250).toInt() // ~1250 steps/km running
            ActivityType.HIKING   -> (distKm * 1400).toInt()
            else -> 0
        }
    }

    private fun updateTimerDisplay() {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        binding.tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            gpsReceiver,
            IntentFilter(GPSTrackingService.ACTION_LOCATION_UPDATE),
            RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        try { unregisterReceiver(gpsReceiver) } catch (_: Exception) {}
    }

    override fun onDestroy() {
        timerHandler.removeCallbacks(timerRunnable)
        if (isTracking) GPSTrackingService.stopTracking(this)
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isTracking) {
            Toast.makeText(this, "Stop your workout before leaving", Toast.LENGTH_SHORT).show()
            return false
        }
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
