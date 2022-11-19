package com.example.forhealth.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CAREGIVERS")
data class Caregivers(
    @PrimaryKey(autoGenerate = true)
    val caregiverId: Int,
    val caregiverName: String,
    val caregiverAvatar: Int,
    val caregiverEmail: String,
    val caregiverPin: Int,
)


@Entity(tableName = "PATIENTS")
data class Patients(
    @PrimaryKey(autoGenerate = true)
    val patientId: Int,
    val patientName: String,
    val caregiverIdInPatient: Int,
    val patientAvatar: Int,
    val patientDate: Long,
    val patientSex: String,
    val patientHeight: Int,
    val patientWeight: Int,
    val patientAge: Int,
    val patientContact: Long
)

@Entity(tableName = "SESSIONS")
data class Sessions(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int,
    val sessionName: String,
    val caregiverIdInSession: Int,
    val patientIdInSession: Int,
    val sessionDate: Long,
    val sessionTime: Long,
)


@Entity(tableName = "EXERCISES")
data class Exercises(
    @PrimaryKey(autoGenerate = true)
    val ExerciseId: Int,
    val caregiverIdInExercise: Int,
    val patientIdInExercise: Int,
    val sessionIdInExercise: Int,
    val exerciseParameters: String,
    val exerciseDate: Long,
    val exerciseTime: Long,
)

@Entity(tableName = "DATA")
data class Data(
    @PrimaryKey(autoGenerate = true)
    val dataId: Int,
    val caregiverIdInData: Int,
    val patientIdInData: Int,
    val sessionIdInData: Int,
    val exerciseIdInData: Int,
    val exerciseDate: Long,
    val exerciseTime: Long,
    val exerciseData: String
)




