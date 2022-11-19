package com.example.forhealth.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import javax.inject.Singleton

@Singleton
@Database(entities = [Caregivers::class,Patients::class,Sessions::class,Exercises::class,Data::class], version = 3)
abstract class MyDatabaseInstance : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao

}