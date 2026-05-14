package com.healthfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ActivityType {
    WALKING, RUNNING, CYCLING, SWIMMING, YOGA,
    GYM, HIKING, DANCING, OTHER
}

@Entity(tableName = "physical_activity")
data class PhysicalActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val activityType: ActivityType,
    val steps: Int = 0,
    val durationMinutes: Int,
    val caloriesBurned: Float = 0f,
    val distanceKm: Float = 0f,
    val heartRateAvg: Int = 0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "",  // "YYYY-MM-DD"
    val time: String = ""   // "HH:mm"
)

@Entity(tableName = "daily_steps")
data class DailySteps(
    @PrimaryKey
    val date: String,       // "YYYY-MM-DD" - primary key so one record per day
    val steps: Int = 0,
    val goal: Int = 10000,
    val caloriesBurned: Float = 0f,
    val distanceKm: Float = 0f
) {
    fun progressPercent(): Int = ((steps.toFloat() / goal) * 100).toInt().coerceAtMost(100)
    fun isGoalReached(): Boolean = steps >= goal
}
