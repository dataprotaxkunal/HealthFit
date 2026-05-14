package com.healthfit.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.healthfit.data.entities.DailySteps
import com.healthfit.data.entities.PhysicalActivity

@Dao
interface PhysicalActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: PhysicalActivity): Long

    @Delete
    suspend fun deleteActivity(activity: PhysicalActivity)

    @Query("SELECT * FROM physical_activity ORDER BY timestamp DESC")
    fun getAllActivities(): LiveData<List<PhysicalActivity>>

    @Query("SELECT * FROM physical_activity WHERE date = :date ORDER BY timestamp ASC")
    fun getActivitiesForDate(date: String): LiveData<List<PhysicalActivity>>

    @Query("SELECT * FROM physical_activity ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 10): LiveData<List<PhysicalActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailySteps(dailySteps: DailySteps)

    @Query("SELECT * FROM daily_steps WHERE date = :date")
    fun getDailySteps(date: String): LiveData<DailySteps?>

    @Query("SELECT * FROM daily_steps ORDER BY date DESC LIMIT :days")
    fun getRecentDailySteps(days: Int = 7): LiveData<List<DailySteps>>

    @Query("SELECT SUM(steps) FROM daily_steps WHERE date >= :startDate AND date <= :endDate")
    fun getTotalStepsForPeriod(startDate: String, endDate: String): LiveData<Int?>

    @Query("SELECT AVG(caloriesBurned) FROM physical_activity WHERE date >= :startDate AND date <= :endDate")
    fun getAvgCaloriesForPeriod(startDate: String, endDate: String): LiveData<Float?>
}
