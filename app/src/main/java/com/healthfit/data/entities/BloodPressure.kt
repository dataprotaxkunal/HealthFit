package com.healthfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure")
data class BloodPressure(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val systolic: Int,      // upper number (mmHg)
    val diastolic: Int,     // lower number (mmHg)
    val pulse: Int = 0,     // beats per minute
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "",  // "YYYY-MM-DD"
    val time: String = ""   // "HH:mm"
) {
    fun getCategory(): BPCategory {
        return when {
            systolic < 90 || diastolic < 60 -> BPCategory.LOW
            systolic < 120 && diastolic < 80 -> BPCategory.NORMAL
            systolic < 130 && diastolic < 80 -> BPCategory.ELEVATED
            systolic < 140 || diastolic < 90 -> BPCategory.HIGH_STAGE1
            systolic >= 180 || diastolic >= 120 -> BPCategory.CRISIS
            else -> BPCategory.HIGH_STAGE2
        }
    }

    fun getFormattedReading(): String = "$systolic/$diastolic mmHg"
}

enum class BPCategory {
    LOW, NORMAL, ELEVATED, HIGH_STAGE1, HIGH_STAGE2, CRISIS
}
