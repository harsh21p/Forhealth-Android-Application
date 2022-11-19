package com.example.forhealth.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton


class DatabaseViewModel(private val dataRepository: DataRepository) : ViewModel()  {
    private val liveData = MutableLiveData<List<String>>(listOf("0","0","0"))
    val liveDataMutable : MutableLiveData<List<String>>
    get() = liveData
    fun setLive(avatarSelected: List<String>){
        this.liveData.value = avatarSelected
    }

    // from database

    // caregiver table

    fun getCaregiver() : LiveData<List<Caregivers>> {
        return dataRepository.getCaregiver()
    }

    fun getCaregiverById(id: Int) : LiveData<List<Caregivers>> {
        return dataRepository.getCaregiverById(id)
    }

    fun getCaregiverByEmail(email: String) : LiveData<List<Caregivers>> {
        return dataRepository.getCaregiverByEmail(email)
    }

    fun insertCaregiver(caregivers: Caregivers){
        viewModelScope.launch(Dispatchers.IO){
            dataRepository.insertCaregiver(caregivers)
        }
    }

    fun deleteCaregiver(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteCaregiver(id)
        }
    }

//     data table

    fun getData() : LiveData<List<Data>> {
        return dataRepository.getData()
    }

    fun getLastEntryFromData() : LiveData<List<Data>> {
        return dataRepository.getLastEntryFromData()
    }

    fun insertData(data: Data){
        viewModelScope.launch(Dispatchers.IO){
            dataRepository.insertData(data)
        }
    }

    fun deleteData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteData(id)
        }
    }

    fun deleteSession(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteSession(id)
        }
    }

    fun deleteSessionByPatient(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteSessionByPatient(id)
        }
    }

    fun deleteExerciseByPatient(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteExerciseByPatient(id)
        }
    }

    fun deletePatient(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deletePatient(id)
        }
    }

    fun deleteDataByPatient(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteDataByPatient(id)
        }
    }

    fun getSessionByPatient(patientId: Int) : LiveData<List<Sessions>> {
        return dataRepository.getSessionByPatient(patientId)
    }

    fun getSessionCaregiverGuest(caregiverId: Int) : LiveData<List<Sessions>> {
        return dataRepository.getSessionCaregiverGuest(caregiverId)
    }

    fun getExerciseBySession(sessionId: Int) : LiveData<List<Exercises>> {
        return dataRepository.getExerciseBySession(sessionId)
    }


    fun getSessionByCaregiver(caregiver: Int) : LiveData<List<Sessions>> {
        return dataRepository.getSessionByCaregiver(caregiver)
    }

    fun getExerciseByCaregiver(caregiver: Int) : LiveData<List<Exercises>> {
        return dataRepository.getExerciseByCaregiver(caregiver)
    }

    fun getPatient() : LiveData<List<Patients>> {
        return dataRepository.getPatient()
    }

    fun getPatientByCaregiver(id: Int) : LiveData<List<Patients>> {
        return dataRepository.getPatientByCaregiver(id)
    }

    fun getPatientById(id: Int) : LiveData<List<Patients>> {
        return dataRepository.getPatientById(id)
    }

    fun insertPatient(patients: Patients){
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertPatient(patients)
        }
    }

    fun insertSession(session:Sessions){
        viewModelScope.launch(Dispatchers.IO) {
             dataRepository.insertSession(session)
        }
    }

    fun insertExercise(exercise:Exercises){
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertExercise(exercise)
        }
    }

    fun deleteExerciseBySession(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteExerciseBySession(id)
        }
    }

    fun deleteDataBySession(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteDataBySession(id)
        }
    }

    fun deleteExerciseById(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteExerciseById(id)
        }
    }

    fun deleteDataByExercise(id:Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteDataByExercise(id)
        }
    }

    fun getExerciseById(id: Int) : LiveData<List<Exercises>> {
        return dataRepository.getExerciseById(id)
    }

    fun updateExercise(exercise: Exercises){
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.updateExercise(exercise)
        }
    }

    fun getSessionById(value: Int) : LiveData<List<Sessions>> {
        return dataRepository.getSessionById(value)
    }

    fun getDataByCaregiver(value: Int): LiveData<List<Data>> {
        return dataRepository.getDataByCaregiver(value)
    }

}