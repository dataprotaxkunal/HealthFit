package com.healthfit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.healthfit.data.dao.BloodGlucoseDao
import com.healthfit.data.dao.BloodPressureDao
import com.healthfit.data.dao.PhysicalActivityDao
import com.healthfit.data.entities.*

class Converters {
    @TypeConverter fun fromMealType(value: MealType): String = value.name
    @TypeConverter fun toMealType(value: String): MealType = MealType.valueOf(value)

    @TypeConverter fun fromActivityType(value: ActivityType): String = value.name
    @TypeConverter fun toActivityType(value: String): ActivityType = ActivityType.valueOf(value)
}

@Database(
    entities = [BloodGlucose::class, BloodPressure::class, PhysicalActivity::class, DailySteps::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bloodGlucoseDao(): BloodGlucoseDao
    abstract fun bloodPressureDao(): BloodPressureDao
    abstract fun physicalActivityDao(): PhysicalActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthfit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
