package com.healthfit.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.healthfit.data.entities.BloodPressure

@Dao
interface BloodPressureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bloodPressure: BloodPressure): Long

    @Update
    suspend fun update(bloodPressure: BloodPressure)

    @Delete
    suspend fun delete(bloodPressure: BloodPressure)

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC")
    fun getAllReadings(): LiveData<List<BloodPressure>>

    @Query("SELECT * FROM blood_pressure WHERE date = :date ORDER BY timestamp ASC")
    fun getReadingsByDate(date: String): LiveData<List<BloodPressure>>

    @Query("SELECT * FROM blood_pressure WHERE date >= :startDate AND date <= :endDate ORDER BY timestamp ASC")
    fun getReadingsByDateRange(startDate: String, endDate: String): LiveData<List<BloodPressure>>

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReading(): LiveData<BloodPressure?>

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReadings(limit: Int = 10): LiveData<List<BloodPressure>>

    @Query("SELECT AVG(systolic) FROM blood_pressure WHERE date >= :startDate AND date <= :endDate")
    fun getAvgSystolicForPeriod(startDate: String, endDate: String): LiveData<Float?>

    @Query("SELECT AVG(diastolic) FROM blood_pressure WHERE date >= :startDate AND date <= :endDate")
    fun getAvgDiastolicForPeriod(startDate: String, endDate: String): LiveData<Float?>
}
