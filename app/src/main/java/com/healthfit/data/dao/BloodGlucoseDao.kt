package com.healthfit.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.healthfit.data.entities.BloodGlucose

@Dao
interface BloodGlucoseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bloodGlucose: BloodGlucose): Long

    @Update
    suspend fun update(bloodGlucose: BloodGlucose)

    @Delete
    suspend fun delete(bloodGlucose: BloodGlucose)

    @Query("SELECT * FROM blood_glucose ORDER BY timestamp DESC")
    fun getAllReadings(): LiveData<List<BloodGlucose>>

    @Query("SELECT * FROM blood_glucose WHERE date = :date ORDER BY timestamp ASC")
    fun getReadingsByDate(date: String): LiveData<List<BloodGlucose>>

    @Query("SELECT * FROM blood_glucose WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp ASC")
    fun getReadingsByDateRange(startDate: String, endDate: String): LiveData<List<BloodGlucose>>

    @Query("SELECT * FROM blood_glucose ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReading(): LiveData<BloodGlucose?>

    @Query("SELECT * FROM blood_glucose ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadings(limit: Int = 10): LiveData<List<BloodGlucose>>

    @Query("SELECT AVG(glucoseLevel) FROM blood_glucose WHERE date >= :startDate AND date <= :endDate")
    fun getAverageForPeriod(startDate: String, endDate: String): LiveData<Float?>

    @Query("SELECT COUNT(*) FROM blood_glucose WHERE date = :date")
    suspend fun getCountForDate(date: String): Int
}
