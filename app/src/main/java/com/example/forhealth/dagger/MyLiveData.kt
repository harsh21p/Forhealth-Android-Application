package com.example.forhealth.dagger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.database.Caregivers
import com.example.forhealth.database.DatabaseDao
import com.example.forhealth.datamodel.ModelExercise
import com.example.forhealth.datamodel.ModelPairedDevices
import java.text.FieldPosition

class MyLiveData(private var databaseDao:DatabaseDao,bluetooth:Services){

     private val liveData = MutableLiveData<List<String>>(listOf("0","0","0"))
     val liveDataMutable : MutableLiveData<List<String>>
          get() = liveData
     fun setLiveData(avatarSelected: List<String>){
          this.liveData.value = avatarSelected
     }

     private val avatarSelected = MutableLiveData<Int>()
     val avatarSelectedMutable : LiveData<Int>
          get() = avatarSelected
     fun setAvatarSelected(avatarSelected: Int){
          this.avatarSelected.value = avatarSelected
     }

     private val avatarSelectedPatient = MutableLiveData<Int>()
     val avatarSelectedPatientMutable : LiveData<Int>
          get() = avatarSelectedPatient
     fun setAvatarSelectedPatient(avatarSelectedPatient: Int){
          this.avatarSelectedPatient.value = avatarSelectedPatient
     }


     private val currentCaregiver = MutableLiveData<Int>(9999)
     val currentCaregiverMutable : LiveData<Int>
          get() = currentCaregiver
     fun setCurrentCaregiver(currentCaregiver: Int){
          this.currentCaregiver.value = currentCaregiver
     }

     private val btConnection = MutableLiveData<Boolean>(false)
     val btConnectionMutable : LiveData<Boolean>
     get() = btConnection
     fun setConnection(btConnection: Boolean){
          this.btConnection.value = btConnection
     }

     private val doctorEmail = MutableLiveData<String>()
     val doctorEmailMutable : LiveData<String>
     get() = doctorEmail
     fun setEmailCurrentCaregiver(btConnection: String){
          this.doctorEmail.value = btConnection
     }

     private val currentPatient = MutableLiveData<Int>(9999)
     val currentPatientMutable : LiveData<Int>
     get() = currentPatient
     fun setCurrentPatient(currentPatient: Int){
          this.currentPatient.value = currentPatient
     }

     private val currentSession = MutableLiveData<Int>(9999)
     val currentSessionMutable : LiveData<Int>
     get() = currentSession
     fun setCurrentSession(currentSession: Int){
          this.currentSession.value = currentSession
     }



     private val currentSessionName = MutableLiveData<String>()
     val currentSessionNameMutable : LiveData<String>
     get() = currentSessionName
     fun setCurrentSessionName(currentSessionName: String){
          this.currentSessionName.value = currentSessionName
     }

     private val currentExercise = MutableLiveData<Int>(9999)
     val currentExerciseMutable : LiveData<Int>
     get() = currentExercise
     fun setCurrentExercise(currentExercise: Int){
          this.currentExercise.value = currentExercise
     }

    private val currentCopy = MutableLiveData<String>("")
    val currentCopyMutable : LiveData<String>
    get() = currentCopy
    fun setCurrentCopy(currentCopy: String){
        this.currentCopy.value = currentCopy
    }

     private val currentMovementList = MutableLiveData<ArrayList<ModelExercise>>()
     val currentMovementListMutable : LiveData<ArrayList<ModelExercise>>
     get() = currentMovementList
    fun setMovementList(exerciseList: ArrayList<ModelExercise>) {
         this.currentMovementList.value = exerciseList
    }

     private val currentPosition = MutableLiveData<Int>()
     val currentPositionMutable : LiveData<Int>
     get() = currentPosition
     fun setCurrentPosition(position: Int) {
          this.currentPosition.value = position
     }

     private val completedRepetition = MutableLiveData<Int>(9999)
     val completedRepetitionMutable : LiveData<Int>
     get() = completedRepetition
     fun setCompletedRepetition(completedRepetition: Int) {
          this.completedRepetition.value = completedRepetition
     }




     private val msg = MutableLiveData<String>("")
     val msgMutable : LiveData<String>
     get() = msg
     fun setMsg(msg: String) {
          this.msg.value = msg
     }

    private val calibratedTorque = MutableLiveData<Float>(0.0f)
    val calibratedTorqueMutable : LiveData<Float>
    get() = calibratedTorque
    fun setCalibratedTorque(calibratedTorque: Float) {
        this.calibratedTorque.value = calibratedTorque
    }

    private val breakState = MutableLiveData<Boolean>(false)
    val breakStateMutable : LiveData<Boolean>
    get() = breakState
    fun setBreakState(breakState: Boolean) {
        this.breakState.value = breakState
    }

    private val isClickable = MutableLiveData<Boolean>(true)
    val isClickableMutable : LiveData<Boolean>
    get() = isClickable
    fun setIsClickable(isClickable: Boolean) {
        this.isClickable.value = isClickable
    }

    val pairedDevicesList = ArrayList<ModelPairedDevices>()
     val pairedDevicesViewHolder:PairedDevicesViewHolder = PairedDevicesViewHolder(pairedDevicesList,bluetooth)

     companion object {
          private var INSTANCE: MyLiveData? = null
          fun getMyLiveData(databaseDao:DatabaseDao,bluetooth:Services): MyLiveData {
               if (INSTANCE == null) {
                    synchronized(this) {
                         INSTANCE = MyLiveData(databaseDao, bluetooth)
                    }
               }
               return INSTANCE!!
          }
     }
}