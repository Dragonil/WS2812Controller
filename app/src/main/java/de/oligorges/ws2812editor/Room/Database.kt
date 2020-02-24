package de.oligorges.ws2812editor.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [stripe::class, led::class, device::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun ledDao(): LedDao
    abstract fun stripeDao(): StripeDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java, "App-database.db")
            .fallbackToDestructiveMigration()
            .build()

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE led ADD COLUMN State INTEGER")
            }
        }
    }




}

/*

@Database(entities = arrayOf(stripe::class), version = 1)
abstract class StripeDatabase : RoomDatabase() {
    abstract fun stripeDao(): StripeDao

}
fun getStripeDatabase(context: Context): StripeDatabase{
    return Room.databaseBuilder(
        context.applicationContext,
        StripeDatabase::class.java, "StripeDatabase"
    ).build()
}

@Database(entities = arrayOf(led::class), version = 1)
abstract class LedDatabase : RoomDatabase() {
    abstract fun ledDao(): LedDao

}

fun getLedDatabase(context: Context): LedDatabase{
    return Room.databaseBuilder(
        context.applicationContext,
        LedDatabase::class.java, "LedDatabase"
    ).build()
}

@Database(entities = arrayOf(device::class), version = 1)
abstract class DeviceDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}

fun getDeviceDatabase(context: Context): DeviceDatabase{
    return Room.databaseBuilder(
        context.applicationContext,
        DeviceDatabase::class.java, "DeviceDatabase"
    ).build()
}

*/
