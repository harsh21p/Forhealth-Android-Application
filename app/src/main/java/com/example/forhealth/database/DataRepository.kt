package com.example.forhealth.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataRepository(private val databaseDao: DatabaseDao) {

    fun getCaregiver(): LiveData<List<Caregivers>> {
        return databaseDao.getCaregiver()
    }

    fun getCaregiverById(id: Int): LiveData<List<Caregivers>> {
        return databaseDao.getCaregiverById(id)
    }

    fun getCaregiverByEmail(email: String): LiveData<List<Caregivers>> {
        return databaseDao.getCaregiverByEmail(email)
    }

    suspend fun insertCaregiver(caregivers: Caregivers){
        databaseDao.insertCaregiver(caregivers)
    }

    suspend fun deleteCaregiver(id:Int){
        databaseDao.deleteCaregiver(id)
        Log.e("test","detete")
    }

    fun getData(): LiveData<List<Data>> {
        return databaseDao.getData()
    }

    fun getLastEntryFromData(): LiveData<List<Data>> {
        return databaseDao.getLastEntryFromData()
    }

    suspend fun insertData(data: Data){
        databaseDao.insertData(data)
    }

    suspend fun deleteData(id:Int){
        databaseDao.deleteData(id)
    }

    suspend fun deleteSession(id:Int){
        databaseDao.deleteSession(id)
    }

    suspend fun deleteSessionByPatient(id: Int) {
        databaseDao.deleteSessionByPatient(id)
    }

    suspend fun deleteDataByPatient(id: Int) {
        databaseDao.deleteDataByPatient(id)
    }

    fun getSessionByCaregiver(caregiver: Int): LiveData<List<Sessions>> {
        return databaseDao.getSessionByCaregiver(caregiver)
    }

    fun getPatient(): LiveData<List<Patients>> {
        return databaseDao.getPatient()
    }

    fun getSessionByPatient(id:Int): LiveData<List<Sessions>> {
        return databaseDao.getSessionByPatient(id)
    }

    fun getExerciseBySession(id:Int): LiveData<List<Exercises>> {
        return databaseDao.getExerciseBySession(id)
    }


    fun getPatientByCaregiver(id:Int): LiveData<List<Patients>> {
        return databaseDao.getPatientByCaregiver(id)
    }

    fun getPatientById(id:Int): LiveData<List<Patients>> {
        return databaseDao.getPatientById(id)
    }

    suspend fun insertPatient(patients: Patients){
        databaseDao.insertPatient(patients)
    }

    suspend fun insertSession(session: Sessions){
        databaseDao.insertSession(session)
    }

    suspend fun deleteExerciseByPatient(id:Int){
        databaseDao.deleteExerciseByPatient(id)
    }

    suspend fun deletePatient(id:Int){
        databaseDao.deletePatient(id)
    }

    suspend fun deletePatientByCaregiver(id:Int){
        databaseDao.deletePatientByCaregiver(id)
    }

    suspend fun insertExercise(exercise:Exercises){
        databaseDao.insertExercise(exercise)
    }

    suspend fun deleteExerciseBySession(id: Int) {
        databaseDao.deleteExerciseBySession(id)
    }

    suspend fun deleteDataBySession(id: Int) {
        databaseDao.deleteDataBySession(id)
    }

    suspend fun deleteExerciseById(id: Int) {
        databaseDao.deleteExerciseById(id)
    }

    suspend fun deleteDataByExercise(id: Int) {
        databaseDao.deleteDataByExercise(id)
    }

    fun getExerciseById(id: Int): LiveData<List<Exercises>> {
        return databaseDao.getExerciseById(id)
    }

    fun getSessionCaregiverGuest(id: Int): LiveData<List<Sessions>> {
        return databaseDao.getSessionCaregiverGuest(id)
    }

    suspend fun updateExercise(exercise: Exercises) {
        databaseDao.updateExercise(exercise)
    }

    fun getSessionById(value: Int): LiveData<List<Sessions>> {
        return databaseDao.getSessionById(value)
    }

    fun getExerciseByCaregiver(caregiver: Int): LiveData<List<Exercises>> {
        return databaseDao.getExerciseByCaregiver(caregiver)
    }

    fun getDataByCaregiver(value: Int): LiveData<List<Data>> {
        return databaseDao.getDataByCaregiver(value)
    }
}
