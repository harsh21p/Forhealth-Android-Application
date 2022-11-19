package com.example.forhealth.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DatabaseDao {

    @Query("SELECT * from CAREGIVERS")
    fun getCaregiver() : LiveData<List<Caregivers>>

    @Query("SELECT * FROM CAREGIVERS WHERE caregiverId = :id")
    fun getCaregiverById(id:Int) : LiveData<List<Caregivers>>

    @Query("SELECT * FROM CAREGIVERS WHERE caregiverEmail = :email")
    fun getCaregiverByEmail(email:String) : LiveData<List<Caregivers>>

    @Insert
    suspend fun insertCaregiver(careGivers: Caregivers)

    @Query("DELETE FROM CAREGIVERS WHERE caregiverId = :id")
    suspend fun deleteCaregiver(id:Int)

    @Query("SELECT * from DATA")
    fun getData(): LiveData<List<Data>>

    @Query("SELECT * from DATA ORDER BY dataId DESC LIMIT 1")
    fun getLastEntryFromData(): LiveData<List<Data>>

    @Insert
    suspend fun insertData(data: Data)

    @Query("DELETE FROM DATA WHERE dataId = :id")
    suspend fun deleteData(id:Int)

    @Query("DELETE FROM SESSIONS WHERE sessionId = :id")
    suspend fun deleteSession(id:Int)

    @Query("DELETE FROM SESSIONS WHERE patientIdInSession = :id")
    suspend fun deleteSessionByPatient(id:Int)

    @Query("DELETE FROM DATA WHERE patientIdInData = :id")
    suspend fun deleteDataByPatient(id:Int)

    @Query("SELECT * from SESSIONS")
    fun getSession(): LiveData<List<Sessions>>

    @Query("SELECT * from SESSIONS WHERE caregiverIdInSession = :caregiver")
    fun getSessionByCaregiver(caregiver:Int): LiveData<List<Sessions>>

    @Insert
    suspend fun insertSession(session: Sessions)

    @Query("SELECT * from PATIENTS")
    fun getPatient(): LiveData<List<Patients>>

    @Query("SELECT * from PATIENTS WHERE patientId = :id")
    fun getPatientById(id:Int): LiveData<List<Patients>>

    @Query("SELECT * from PATIENTS WHERE caregiverIdInPatient = :caregiver")
    fun getPatientByCaregiver(caregiver:Int): LiveData<List<Patients>>

    @Query("SELECT * from SESSIONS WHERE patientIdInSession = :id")
    fun getSessionByPatient(id:Int): LiveData<List<Sessions>>


    @Query("SELECT * from EXERCISES WHERE sessionIdInExercise = :id")
    fun getExerciseBySession(id:Int): LiveData<List<Exercises>>

    @Insert
    suspend fun insertPatient(patient: Patients)

    @Query("DELETE FROM EXERCISES WHERE patientIdInExercise = :id")
    suspend fun deletePatient(id:Int)

    @Query("DELETE FROM PATIENTS WHERE patientId = :id")
    suspend fun deleteExerciseByPatient(id:Int)

    @Query("DELETE FROM PATIENTS WHERE caregiverIdInPatient = :id")
    suspend fun deletePatientByCaregiver(id:Int)

    @Query("DELETE FROM EXERCISES WHERE sessionIdInExercise = :id")
    suspend fun deleteExerciseBySession(id:Int)

    @Insert
    suspend fun insertExercise(exercises: Exercises)

    @Query("DELETE FROM DATA WHERE sessionIdInData = :id")
    suspend fun deleteDataBySession(id:Int)

    @Query("DELETE FROM EXERCISES WHERE ExerciseId = :id")
    suspend fun deleteExerciseById(id: Int)

    @Query("DELETE FROM DATA WHERE exerciseIdInData = :id")
    suspend fun deleteDataByExercise(id: Int)

    @Query("SELECT * FROM EXERCISES WHERE ExerciseId = :id")
    fun getExerciseById(id: Int): LiveData<List<Exercises>>

    @Query("SELECT * FROM SESSIONS WHERE caregiverIdInSession = :id and patientIdInSession = 9999")
    fun getSessionCaregiverGuest(id: Int): LiveData<List<Sessions>>

    @Update
    suspend fun updateExercise(exercise: Exercises)

    @Query("SELECT * from SESSIONS WHERE sessionId = :value")
    fun getSessionById(value: Int): LiveData<List<Sessions>>

    @Query("SELECT * FROM EXERCISES WHERE caregiverIdInExercise = :caregiver")
    fun getExerciseByCaregiver(caregiver: Int): LiveData<List<Exercises>>

    @Query("SELECT * FROM DATA WHERE caregiverIdInData = :value")
    fun getDataByCaregiver(value: Int): LiveData<List<Data>>


}