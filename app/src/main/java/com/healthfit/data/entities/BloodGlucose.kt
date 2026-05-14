package com.healthfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MealType { PRE_MEAL, POST_MEAL, FASTING, BEDTIME, RANDOM }

@Entity(tableName = "blood_glucose")
data class BloodGlucose(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val glucoseLevel: Float,        // in mg/dL
    val mealType: MealType,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "",          // "YYYY-MM-DD"
    val time: String = ""           // "HH:mm"
) {
    fun getStatus(): GlucoseStatus {
        return when (mealType) {
            MealType.FASTING -> when {
                glucoseLevel < 70 -> GlucoseStatus.LOW
                glucoseLevel <= 100 -> GlucoseStatus.NORMAL
                glucoseLevel <= 125 -> GlucoseStatus.PRE_DIABETIC
                else -> GlucoseStatus.HIGH
            }
            MealType.POST_MEAL -> when {
                glucoseLevel < 70 -> GlucoseStatus.LOW
                glucoseLevel <= 140 -> GlucoseStatus.NORMAL
                glucoseLevel <= 199 -> GlucoseStatus.PRE_DIABETIC
                else -> GlucoseStatus.HIGH
            }
            MealType.PRE_MEAL -> when {
                glucoseLevel < 70 -> GlucoseStatus.LOW
                glucoseLevel <= 130 -> GlucoseStatus.NORMAL
                glucoseLevel <= 179 -> GlucoseStatus.PRE_DIABETIC
                else -> GlucoseStatus.HIGH
            }
            else -> when {
                glucoseLevel < 70 -> GlucoseStatus.LOW
                glucoseLevel <= 140 -> GlucoseStatus.NORMAL
                glucoseLevel <= 199 -> GlucoseStatus.PRE_DIABETIC
                else -> GlucoseStatus.HIGH
            }
        }
    }
}

enum class GlucoseStatus { LOW, NORMAL, PRE_DIABETIC, HIGH }
