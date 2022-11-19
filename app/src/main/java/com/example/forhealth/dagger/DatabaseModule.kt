package com.example.forhealth.dagger

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.forhealth.database.MyDatabaseInstance
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(context: Context): MyDatabaseInstance {
        val migration_1_2 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE PATIENTS ADD COLUMN identifier INTEGER NOT NULL DEFAULT(1)")
                database.execSQL("ALTER TABLE PATIENTS ADD COLUMN patientAge INTEGER NOT NULL DEFAULT(1)")
            }
        }
        return Room.databaseBuilder(
            context,
            MyDatabaseInstance::class.java,
            "forhealthDB")
            .addMigrations(migration_1_2)
            .build()
    }
}