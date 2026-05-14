package com.healthfit.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.healthfit.data.AppDatabase
import com.healthfit.data.HealthRepository
import com.healthfit.data.entities.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealthRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val todayDate: String get() = dateFormat.format(Date())

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HealthRepository(db)
    }

    // ── Blood Glucose ──────────────────────────────────────────────────────
    val allGlucoseReadings = repository.allGlucoseReadings
    val latestGlucose = repository.latestGlucose
    val todayGlucose: LiveData<List<BloodGlucose>> get() = repository.getGlucoseByDate(todayDate)

    fun insertGlucose(level: Float, mealType: MealType, notes: String = "") {
        viewModelScope.launch {
            val now = Date()
            repository.insertGlucose(
                BloodGlucose(
                    glucoseLevel = level,
                    mealType = mealType,
                    notes = notes,
                    timestamp = now.time,
                    date = dateFormat.format(now),
                    time = timeFormat.format(now)
                )
            )
        }
    }

    fun deleteGlucose(reading: BloodGlucose) = viewModelScope.launch { repository.deleteGlucose(reading) }

    // ── Blood Pressure ─────────────────────────────────────────────────────
    val allBPReadings = repository.allBPReadings
    val latestBP = repository.latestBP
    val todayBP: LiveData<List<BloodPressure>> get() = repository.getBPByDate(todayDate)

    fun insertBP(systolic: Int, diastolic: Int, pulse: Int = 0, notes: String = "") {
        viewModelScope.launch {
            val now = Date()
            repository.insertBP(
                BloodPressure(
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    notes = notes,
                    timestamp = now.time,
                    date = dateFormat.format(now),
                    time = timeFormat.format(now)
                )
            )
        }
    }

    fun deleteBP(reading: BloodPressure) = viewModelScope.launch { repository.deleteBP(reading) }

    // ── Physical Activity ──────────────────────────────────────────────────
    val allActivities = repository.allActivities
    val todaySteps: LiveData<DailySteps?> get() = repository.getDailySteps(todayDate)
    val recentWeekSteps = repository.getRecentDailySteps(7)

    fun insertActivity(type: ActivityType, durationMins: Int, calories: Float,
                       distance: Float, steps: Int = 0, notes: String = "") {
        viewModelScope.launch {
            val now = Date()
            val today = dateFormat.format(now)
            repository.insertActivity(
                PhysicalActivity(
                    activityType = type,
                    durationMinutes = durationMins,
                    caloriesBurned = calories,
                    distanceKm = distance,
                    steps = steps,
                    notes = notes,
                    timestamp = now.time,
                    date = today,
                    time = timeFormat.format(now)
                )
            )
            // Update daily steps if walking/running
            if (steps > 0) {
                val existing = repository.getDailySteps(today).value
                val totalSteps = (existing?.steps ?: 0) + steps
                val totalCal = (existing?.caloriesBurned ?: 0f) + calories
                val totalDist = (existing?.distanceKm ?: 0f) + distance
                repository.upsertDailySteps(
                    DailySteps(date = today, steps = totalSteps, caloriesBurned = totalCal, distanceKm = totalDist)
                )
            }
        }
    }

    fun deleteActivity(activity: PhysicalActivity) = viewModelScope.launch { repository.deleteActivity(activity) }
}

class HealthViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
