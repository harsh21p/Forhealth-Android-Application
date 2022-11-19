package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.ExerciseViewHolder
import com.example.forhealth.dagger.*
import com.example.forhealth.database.*
import com.example.forhealth.databinding.SessonsInformationBinding
import com.example.forhealth.datamodel.ModelExercise
import com.example.forhealth.datamodel.ModelScheduledSession
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SessionInformation : AppCompatActivity(){

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var movementViewHolder: ExerciseViewHolder?=null
    private var exerciseList = ArrayList<ModelExercise>()
    @Inject
    @CommonQualifier
    lateinit var common : Services
    @Inject
    @BluetoothQualifier
    lateinit var bluetooth : Services
    var check = false
    @Inject
    lateinit var myDatabaseInstance : MyDatabaseInstance
    lateinit var mainViewModel : DatabaseViewModel
    lateinit var myLiveData : MyLiveData
    private lateinit var binding : SessonsInformationBinding
    private var exist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this, R.layout.sessons_information)

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        val dao = myDatabaseInstance.databaseDao()
        val repository = DataRepository(dao)

        myLiveData = MyLiveData.getMyLiveData(dao,bluetooth)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(
            DatabaseViewModel::class.java)


        myLiveData.liveDataMutable.observe(this, androidx.lifecycle.Observer {
            binding.tourqe = it[0]+" nm"
            binding.angle = it[1]+" deg"
            binding.speed = it[2]+" rpm"
//            bluetooth.sendMessage("send")

        })
        myLiveData.btConnectionMutable.observe(this, androidx.lifecycle.Observer {
            if(myLiveData.btConnectionMutable.value==true) {
                check = true
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                binding.controls!!.visibility = View.VISIBLE
            }else{
                if(check) {
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                    check = false
                    binding.controls!!.visibility = View.GONE
                    binding.allControlsCard!!.visibility = View.GONE
                }
            }
        })

        binding.backButton!!.setOnClickListener(View.OnClickListener {
            myLiveData.setCurrentExercise(9999)
            if(myLiveData.currentPatientMutable.value != 9999) {
                startActivity(Intent(this, PatientProfilePage::class.java))
            }else{
                startActivity(Intent(this, GuestMode::class.java))
            }
            finish()
        })

        bluetoothSetup()

        binding.playSession!!.setOnClickListener(View.OnClickListener {
            if(exist) {
                startActivity(Intent(this@SessionInformation, SessionControlPanel::class.java))
                finish()
            }else{
                Toast.makeText(this,"Movement not found",Toast.LENGTH_SHORT).show()
            }
        })

        binding.mainLayout!!.setOnClickListener(View.OnClickListener {
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
        })

        movementViewHolder = ExerciseViewHolder(exerciseList,this)

        val movementRecyclerView = findViewById<RecyclerView>(R.id.movement_recycler)
        movementRecyclerView.layoutManager = LinearLayoutManager(this@SessionInformation,
            LinearLayoutManager.HORIZONTAL,false)
        movementRecyclerView.adapter =  movementViewHolder
        addMovement()

        myLiveData.currentCopyMutable.observe(this, androidx.lifecycle.Observer {
            if(it.isEmpty()){
                binding.pasteButton!!.visibility=View.GONE
            }else{
                binding.pasteButton!!.visibility=View.VISIBLE
            }
        })

        binding.pasteButton!!.setOnClickListener(View.OnClickListener {
            val currentDateTime: String = sdf.format(Date())
            val currentDate = currentDateTime.split('_')
            mainViewModel.insertExercise(Exercises(0,myLiveData.currentCaregiverMutable.value!!,
                myLiveData.currentPatientMutable.value!!,
                myLiveData.currentSessionMutable.value!!,
                myLiveData.currentCopyMutable.value!!,
                currentDate[0].toLong(),
                currentDate[1].toLong()))
            myLiveData.setCurrentCopy("")
            addMovement()
        })

        binding.txtnext!!.text = myLiveData.currentSessionNameMutable.value
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun addMovement() {
        var once =  true

        mainViewModel.getExerciseBySession(myLiveData.currentSessionMutable.value!!).observe(this,
            androidx.lifecycle.Observer {
                exerciseList.clear()
                movementViewHolder!!.notifyDataSetChanged()

                var calFound = false
                var calMove:ModelExercise? = null
                var smallest = 360
                var greatest = -360
                var ifExist = false
                var data = ""

                if (!it.isNullOrEmpty()) {
                    exist = true
                    var z = 1
                    for (i in it!!) {
                        var array = i.exerciseParameters.split(",")
                        if(array[13] == "AR ( ISOTONIC )" || array[14] == "AR ( ISOTONIC )" || array[14] == "ACTIVE" || array[13] == "ACTIVE"){
                            ifExist = true
                            Log.e("TEST","Exist")
                            z = 2
                        }
                        if(array[0]=="CAL"){
                            calMove =  ModelExercise(1,
                                "Movement 1",
                                array[0],
                                array[1],
                                array[2],
                                array[3],
                                array[4],
                                array[5],
                                array[6],
                                array[7],
                                array[8],
                                array[9],
                                array[10],
                                array[11],
                                array[12],
                                array[13],
                                array[14],
                                i.ExerciseId)
                            myLiveData.setCurrentExercise(i.ExerciseId)
                            calFound = true
                            Log.e("TEST","CAL Found")
                        }else{
                            exerciseList.add(ModelExercise(1,"Movement $z",array[0],array[1],array[2],array[3],array[4],array[5],array[6],array[7],array[8],array[9],array[10],array[11],array[12],array[13],array[14],i.ExerciseId))
                            greatest = findGreatest( arrayOf(array[1].toInt(),array[2].toInt(),array[3].toInt(),greatest))
                            smallest = findSmallest(arrayOf(array[1].toInt(),array[2].toInt(),array[3].toInt(),smallest))
                            Log.e("TEST","Adding")
                        }
                    }
                }else{
                    exist = false
                }

                if(ifExist){
                    val currentDateTime: String = sdf.format(Date())
                    val currentDate = currentDateTime.split('_')

                    data = "CAL,$smallest,$greatest,$smallest,1,50,0,0,0,50,0,0,0,PASSIVE,PASSIVE"

                    if(calFound && once){
                        mainViewModel.updateExercise(Exercises(myLiveData.currentExerciseMutable.value!!,myLiveData.currentCaregiverMutable.value!!,myLiveData.currentPatientMutable.value!!,myLiveData.currentSessionMutable.value!!,data,currentDate[0].toLong(),currentDate[1].toLong()))
                        once = false
                        Log.e("TEST","Updating")

                    }else{
                        if(once) {
                            mainViewModel.insertExercise(Exercises(0,
                                myLiveData.currentCaregiverMutable.value!!,
                                myLiveData.currentPatientMutable.value!!,
                                myLiveData.currentSessionMutable.value!!,
                                data,
                                currentDate[0].toLong(),
                                currentDate[1].toLong()))
                            once = false
                            Log.e("TEST","New cal")

                        }
                    }
                    if(calFound){
                        exerciseList.add(0,calMove!!)
                    }
                }
                Log.e("TEST","Add button")

                exerciseList.add(ModelExercise(2,"","","","","","","","","","","","","","","","",9999))
                movementViewHolder!!.notifyDataSetChanged()
                Log.e("TEST","List Updated")

                myLiveData.setMovementList(exerciseList)
                myLiveData.setCurrentExercise(9999)
            })
    }


    private fun findSmallest(arrayOf: Array<Int>): Int {
        arrayOf.sort()
        return arrayOf[0]
    }

    private fun findGreatest(arrayOf: Array<Int>): Int {
        arrayOf.sort()
        return arrayOf[arrayOf.size - 1]
    }

    private fun bluetoothSetup() {
        common.setClickForSideBar(binding.hamburger!!,binding.controls!!,binding.sidebar!!,binding.allControlsCard!!,this)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,binding.sidebar!!,binding.allControlsCard!!,binding.controls!!,binding.controlShutdown!!,binding.controlReset!!,binding.controlSetHome!!,binding.controlClose!!,binding.controlDirectionClockwise!!,binding.controlDirectionAnticlockwise!!,binding.controlBrakeState!!,binding.controlRefresh!!,this,binding.speedSlider!!)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = myLiveData.pairedDevicesViewHolder

        binding.speedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.speedValue!!.text= value.toInt().toString()+" %"
        }
    }

    fun deleteButton(position: Int) {
        myLiveData.setCurrentExercise(exerciseList[position].id)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_delete,null)
        dialogBoxDelete(view,position)

    }

    fun editButton(position: Int) {
        myLiveData.setCurrentExercise(exerciseList[position].id)
        startActivity(Intent(this@SessionInformation, GuestMode::class.java))
        finish()
    }

    fun onExerciseClick(position: Int) {
        myLiveData.setCurrentExercise(exerciseList[position].id)
        if((exerciseList.size - 1) == position) {
            startActivity(Intent(this@SessionInformation, GuestMode::class.java))
            finish()
        }
    }

    private fun dialogBoxDelete(view: View, position: Int) {

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.delete_dialog_yes_button)
        val  no = view.findViewById<CardView>(R.id.delete_dialog_cancel_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            mainViewModel.deleteExerciseById(myLiveData.currentExerciseMutable.value!!)
            mainViewModel.deleteDataByExercise(myLiveData.currentExerciseMutable.value!!)
            addMovement()
            builder.dismiss()
        }
        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    fun copyButton(position: Int) {
        myLiveData.setCurrentCopy(exerciseList[position].exerciseType+","+exerciseList[position].firstAngle+","+exerciseList[position].secondAngle+","+exerciseList[position].thirdAngle+","+exerciseList[position].repetition+","+exerciseList[position].firstMovementSpeed+","+exerciseList[position].firstMovementAssistance+","+exerciseList[position].firstMovementResistance+","+exerciseList[position].firstMovementHoldTime+","+exerciseList[position].secondMovementSpeed+","+exerciseList[position].secondMovementAssistance+","+exerciseList[position].secondMovementResistance+","+exerciseList[position].secondMovementHoldTime+","+exerciseList[position].firstMovementType+","+exerciseList[position].secondMovementType)
        Toast.makeText(this,"copied",Toast.LENGTH_SHORT).show()
    }
}