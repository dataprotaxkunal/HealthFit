package com.healthfit.data

import com.healthfit.data.entities.*

class HealthRepository(database: AppDatabase) {

    private val glucoseDao = database.bloodGlucoseDao()
    private val bpDao = database.bloodPressureDao()
    private val activityDao = database.physicalActivityDao()

    // ── Blood Glucose ──────────────────────────────────────────────────────
    val allGlucoseReadings = glucoseDao.getAllReadings()
    val latestGlucose = glucoseDao.getLatestReading()

    suspend fun insertGlucose(reading: BloodGlucose) = glucoseDao.insert(reading)
    suspend fun updateGlucose(reading: BloodGlucose) = glucoseDao.update(reading)
    suspend fun deleteGlucose(reading: BloodGlucose) = glucoseDao.delete(reading)

    fun getGlucoseByDate(date: String) = glucoseDao.getReadingsByDate(date)
    fun getGlucoseByDateRange(start: String, end: String) = glucoseDao.getReadingsByDateRange(start, end)
    fun getRecentGlucose(limit: Int = 10) = glucoseDao.getRecentReadings(limit)
    fun getAvgGlucose(start: String, end: String) = glucoseDao.getAverageForPeriod(start, end)

    // ── Blood Pressure ─────────────────────────────────────────────────────
    val allBPReadings = bpDao.getAllReadings()
    val latestBP = bpDao.getLatestReading()

    suspend fun insertBP(reading: BloodPressure) = bpDao.insert(reading)
    suspend fun updateBP(reading: BloodPressure) = bpDao.update(reading)
    suspend fun deleteBP(reading: BloodPressure) = bpDao.delete(reading)

    fun getBPByDate(date: String) = bpDao.getReadingsByDate(date)
    fun getBPByDateRange(start: String, end: String) = bpDao.getReadingsByDateRange(start, end)
    fun getRecentBP(limit: Int = 10) = bpDao.getRecentReadings(limit)

    // ── Physical Activity ──────────────────────────────────────────────────
    val allActivities = activityDao.getAllActivities()

    suspend fun insertActivity(activity: PhysicalActivity) = activityDao.insertActivity(activity)
    suspend fun deleteActivity(activity: PhysicalActivity) = activityDao.deleteActivity(activity)
    suspend fun upsertDailySteps(steps: DailySteps) = activityDao.insertOrUpdateDailySteps(steps)

    fun getDailySteps(date: String) = activityDao.getDailySteps(date)
    fun getRecentDailySteps(days: Int = 7) = activityDao.getRecentDailySteps(days)
    fun getActivitiesForDate(date: String) = activityDao.getActivitiesForDate(date)
    fun getRecentActivities(limit: Int = 10) = activityDao.getRecentActivities(limit)
}
