package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.DropdownMovementViewHolder
import com.example.forhealth.adapter.DropdownSessionViewHolder
import com.example.forhealth.dagger.*
import com.example.forhealth.database.*
import com.example.forhealth.databinding.GuestModeBinding
import com.example.forhealth.datamodel.ModelExercise
import com.example.forhealth.datamodel.ModelMovement
import com.example.forhealth.datamodel.ModelPairedDevices
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.guest_mode.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GuestMode : AppCompatActivity() {

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var firstSelectExerciseButton = false
    private var secondSelectExerciseButton = false

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

    private lateinit var binding : GuestModeBinding

    private var firstAssistance = 0
    private var secondAssistance = 0
    private var dropdownMovementViewHolder: DropdownMovementViewHolder?=null
    private var dropdownSessionViewHolder: DropdownSessionViewHolder?=null
    private var movementList = ArrayList<ModelMovement>()
    private var sessionList = ArrayList<ModelPairedDevices>()
    private var arr: Array<String>? = null
    private var update = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this, R.layout.guest_mode)

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
            if(myLiveData.currentExerciseMutable.value != 9999){
                startActivity( Intent(this, SessionInformation::class.java))
                finish()
            }else{
                if(myLiveData.currentCaregiverMutable.value == 9999) {
                    startActivity( Intent(this, DoctorsLoginPage::class.java))
                    finish()
                }else{
                    startActivity( Intent(this, DoctorsLandingPage::class.java))
                    finish()
                }
            }

        })

        binding.viewMovementButton!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SessionInformation::class.java))
            finish()
        })

        arr = arrayOf("HIP", "KNEE", "ANKLE")
        var i = 0
        while (i< arr!!.size){
            var movement = ModelMovement(arr!![i])
            movementList.add(movement)
            i++
        }

        binding.givenAngleTwo!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.givenAngleThree!!.text = binding.givenAngleTwo!!.text
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })

        if(myLiveData.currentExerciseMutable.value != 9999) {

            mainViewModel.getExerciseById(myLiveData.currentExerciseMutable.value!!).observe(this,
                Observer {
                    if(!it.isNullOrEmpty()){
                        for (i in it!!) {
                            update = true
                            setDataToUi(i)
                            exerciseVisibilityManager()
                        }
                    }

                })
        }

        bluetoothSetup()

        binding.dropdownSelectSessionButton!!.setOnClickListener(View.OnClickListener {
            if(sessionList.isNotEmpty() && binding.dropdownSessions!!.visibility==View.GONE) {
                binding.dropdownSessions!!.visibility = View.VISIBLE
            }else{
                Toast.makeText(this,"Not found",Toast.LENGTH_SHORT).show()
            }
        })

        dropdownMovementViewHolder = DropdownMovementViewHolder(movementList,this)
        dropdownSessionViewHolder = DropdownSessionViewHolder(sessionList,this)

        val dropdownMovementRecyclerView = findViewById<RecyclerView>(R.id.dropdown_movement_recyclerview)
        dropdownMovementRecyclerView.layoutManager = LinearLayoutManager(this)
        dropdownMovementRecyclerView.adapter =  dropdownMovementViewHolder

        val dropdownSessionRecyclerView = findViewById<RecyclerView>(R.id.session_recyclerview)
        dropdownSessionRecyclerView.layoutManager = LinearLayoutManager(this)
        dropdownSessionRecyclerView.adapter =  dropdownSessionViewHolder

        sessionAdder()

        binding.mainLayout!!.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
            binding.dropdownExercises!!.visibility = View.GONE
            binding.dropdownMovement!!.visibility = View.GONE
            binding.dropdownSessions!!.visibility=View.GONE
            firstSelectExerciseButton = false
            secondSelectExerciseButton = false

        })

        binding.givenAngleTwo!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.givenAngleThree!!.text = binding.givenAngleTwo!!.text
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.givenAngleThree!!.text = binding.givenAngleTwo!!.text
            }
        })

        binding.firstAssistanceSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.firstAssistanceValue!!.text= value.toInt().toString()+" %"
            firstAssistance = value.toInt()
        }

        binding.secondAssistanceSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.secondAssistanceValue!!.text= value.toInt().toString()+" %"
            secondAssistance = value.toInt()
        }

        binding.firstSpeedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.firstSpeedValue!!.text= value.toInt().toString()+" %"
        }

        binding.secondSpeedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.secondSpeedSliderValue!!.text= value.toInt().toString()+" %"
        }

        binding.saveButton!!.setOnClickListener(View.OnClickListener {
            addData()
        })

        binding.firstSelectExerciseButton!!.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            binding.dropdownExercises!!.visibility=View.VISIBLE
            firstSelectExerciseButton = true
            secondSelectExerciseButton = false
        })

        binding.secondSelectExerciseButton!!.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            binding.dropdownExercises!!.visibility=View.VISIBLE

            secondSelectExerciseButton = true
            firstSelectExerciseButton = false
        })
        buttonClick()

    }

    private fun buttonClick() {
        binding.passiveButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="PASSIVE"
                firstAssistance = 0
            }
            else{
                if(secondSelectExerciseButton) {
                    binding.secondSelectExerciseText!!.text = "PASSIVE"
                    secondAssistance = 0
                }
            }
            exerciseVisibilityManager()
        })

        binding.activeAssistedButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="ACTIVE ASSISTED"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    binding.secondSelectExerciseText!!.text="ACTIVE ASSISTED"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })
        binding.activeResistedIsometricButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="AR ( ISOMETRIC )"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    binding.secondSelectExerciseText!!.text="AR ( ISOMETRIC )"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })


        binding.activeResistedIsotonicButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="AR ( ISOTONIC )"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    binding.secondSelectExerciseText!!.text="AR ( ISOTONIC )"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })

        binding.activeButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="ACTIVE"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    binding.secondSelectExerciseText!!.text="ACTIVE"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })

        val view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)

        binding.activeGravityEliminatedButton!!.setOnClickListener(View.OnClickListener {
            common.comingSoonDialogBox(view,this)
        })
        binding.activeResistedIsoKineticButton!!.setOnClickListener(View.OnClickListener {
            common.comingSoonDialogBox(view,this)
        })

        binding.dropdownChooseMovementButton!!.setOnClickListener(View.OnClickListener {
            if(binding.dropdownMovement!!.visibility == View.VISIBLE) {
                binding.dropdownMovement!!.visibility = View.GONE
            }else{
                binding.dropdownMovement!!.visibility = View.VISIBLE
            }
        })
        binding.noneButton!!.setOnClickListener(View.OnClickListener {
            binding.dropdownExercises!!.visibility=View.GONE
            if(firstSelectExerciseButton){
                binding.firstSelectedExerciseText!!.text="None"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    binding.secondSelectExerciseText!!.text="None"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })
    }

    private fun addData(){

        if(binding.firstSelectedExerciseText!!.text == "None" || binding.secondSelectExerciseText!!.text == "None" ){
            Toast.makeText(this,"Exercise type not selected",Toast.LENGTH_SHORT).show()
        }else {
            if (binding.givenRepetitions!!.text!!.isBlank()) {
                Toast.makeText(this, "Repetitions cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.givenAngleOne!!.text!!.isBlank() || binding.givenAngleTwo!!.text!!.isBlank() || binding.givenAngleThree!!.text!!.isBlank() || binding.givenAngleFour!!.text!!.isBlank()) {
                    Toast.makeText(this, "Angle cannot be empty", Toast.LENGTH_SHORT).show()
                } else {

                    val currentDateTime: String = sdf.format(Date())
                    val currentDate = currentDateTime.split('_')
                    val data: String = getMove()

                    if(update){

                        try {
                            // update data to exercise table
                            mainViewModel.updateExercise(Exercises(myLiveData.currentExerciseMutable.value!!,myLiveData.currentCaregiverMutable.value!!,myLiveData.currentPatientMutable.value!!,myLiveData.currentSessionMutable.value!!,data,currentDate[0].toLong(),currentDate[1].toLong()))
                                val iSessionInformationPage = Intent(this@GuestMode, SessionInformation::class.java)
                                startActivity(iSessionInformationPage)
                                finish()

                        } catch (e: Exception) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }else{
                            try {
                                mainViewModel.insertExercise(Exercises(0,myLiveData.currentCaregiverMutable.value!!,
                                    myLiveData.currentPatientMutable.value!!,
                                    myLiveData.currentSessionMutable.value!!,
                                    data,
                                    currentDate[0].toLong(),
                                    currentDate[1].toLong()))

                                    val iSessionInformationPage = Intent(this@GuestMode, SessionInformation::class.java)
                                    startActivity(iSessionInformationPage)
                                    finish()

                            } catch (e: Exception) {
                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }
        }
    }

    private fun getMove(): String {
        return binding.selectedMovementType!!.text.toString() + "," + binding.givenAngleOne!!.text.toString()+","+binding.givenAngleTwo!!.text.toString()+","+binding.givenAngleFour!!.text.toString()+","+binding.givenRepetitions!!.text.toString()+","+binding.firstSpeedSlider!!.value.toInt().toString()+","+binding.firstAssistanceSlider!!.value.toInt().toString()+","+binding.firstAssistanceSlider!!.value.toInt().toString()+","+binding.givenHoldTime!!.text.toString()+","+binding.secondSpeedSlider!!.value.toInt().toString()+","+binding.secondAssistanceSlider!!.value.toInt().toString()+","+binding.secondAssistanceSlider!!.value.toInt().toString()+","+binding.givenHoldTime!!.text.toString()+","+binding.firstSelectedExerciseText!!.text.toString()+","+binding.secondSelectExerciseText!!.text.toString()
    }



    private fun moveToLastSession() {
        if(sessionList.size>1) {
            myLiveData.setCurrentSession( sessionList[(sessionList!!.size - 2)].deviceId.toInt())
            myLiveData.setCurrentSessionName(sessionList[(sessionList!!.size - 2)].deviceName)
            binding.selectedSessionText!!.text= myLiveData.currentSessionNameMutable.value!!
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sessionAdder() {
        if(myLiveData.currentPatientMutable.value != 9999){
            mainViewModel.getSessionByPatient(myLiveData.currentPatientMutable.value!!)
                .observe(this,
                    Observer {
                        sessionList.clear()
                        if(!it.isNullOrEmpty()){
                            for (i in it!!) {
                                var session = ModelPairedDevices(i.sessionName,i.sessionId.toString())
                                sessionList.add(session)
                            }

                            var session = ModelPairedDevices("Add session","0")
                            sessionList.add(session)

                            if(myLiveData.currentSessionMutable.value != 9999){
                                mainViewModel.getSessionById(myLiveData.currentSessionMutable.value!!).observe(this,
                                    Observer {
                                        if(!it.isNullOrEmpty()){
                                            for (i in it!!) {
                                                myLiveData.setCurrentSessionName(i.sessionName)
                                                binding.selectedSessionText!!.text= myLiveData.currentSessionNameMutable.value!!
                                            }
                                        }
                                    })
                            }else{
                                moveToLastSession()
                            }

                        }
                        dropdownSessionViewHolder!!.notifyDataSetChanged()
                    })
        }else{
            mainViewModel.getSessionCaregiverGuest(myLiveData.currentCaregiverMutable.value!!).observe(this,
                Observer {
                    sessionList.clear()
                    if(!it.isNullOrEmpty()){
                        for (i in it!!) {
                            var session = ModelPairedDevices(i.sessionName,i.sessionId.toString())
                            sessionList.add(session)
                        }

                        var session = ModelPairedDevices("Add session","0")
                        sessionList.add(session)
                        if(myLiveData.currentSessionMutable.value != 9999){
                            mainViewModel.getSessionById(myLiveData.currentSessionMutable.value!!).observe(this,
                                Observer {
                                    if(!it.isNullOrEmpty()){
                                        for (i in it!!) {
                                            myLiveData.setCurrentSessionName(i.sessionName)
                                            binding.selectedSessionText!!.text= myLiveData.currentSessionNameMutable.value!!
                                        }
                                    }
                                })
                        }else{
                            moveToLastSession()
                        }
                    }else{
                        val currentDateTime: String = sdf.format(Date())
                        val currentDate = currentDateTime.split('_')
                        mainViewModel.insertSession(Sessions(0,"GS 1",myLiveData.currentCaregiverMutable.value!!,9999,currentDate[0].toLong(),currentDate[1].toLong()))

                    }
                    dropdownSessionViewHolder!!.notifyDataSetChanged()
                })
        }

    }

    private fun setDataToUi(i: Exercises) {
        var list = i.exerciseParameters.split(",")
        var model = ModelExercise(1,"Movement",list[0],list[1],list[2],list[3],list[4],list[5],list[6],list[7],list[8],list[9],list[10],list[11],list[12],list[13],list[14],i.ExerciseId)

        binding.givenRepetitions!!.setText(model.repetition)
        binding.firstSelectedExerciseText!!.text = model.firstMovementType
        binding.secondSelectExerciseText!!.text = model.secondMovementType
        binding.givenAngleOne!!.setText(model.firstAngle)
        binding.givenAngleTwo!!.setText(model.secondAngle)
        binding.givenAngleFour!!.setText(model.thirdAngle)
        binding.firstSpeedSlider!!.value = model.firstMovementSpeed.toFloat()
        binding.firstSpeedValue!!.text = model.firstMovementSpeed
        binding.secondSpeedSlider!!.value = model.secondMovementSpeed.toFloat()
        binding.secondSpeedSliderValue!!.text = model.secondMovementSpeed

        binding.secondAssistanceSlider!!.value = model.secondMovementAssistance.toFloat()
        binding.secondAssistanceValue!!.text = model.secondMovementAssistance

        binding.firstAssistanceSlider!!.value = model.firstMovementAssistance.toFloat()
        binding.firstAssistanceValue!!.text = model.firstMovementAssistance

        binding.selectedMovementType!!.text = model.exerciseType

        binding.givenHoldTime!!.setText(model.firstMovementHoldTime)

    }

    @SuppressLint("SetTextI18n")
    private fun exerciseVisibilityManager() {
        binding.holdTimeCard!!.visibility=View.GONE
        binding.firstAssistanceCard!!.visibility=View.GONE
        binding.secondAssistanceCard!!.visibility=View.GONE

        if(binding.firstSelectedExerciseText!!.text =="ACTIVE ASSISTED"){
            binding.firstAssistanceCard!!.visibility=View.VISIBLE
            binding.firstAssistanceText!!.text="Assistance"
            binding.firstAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                R.drawable.picture_assistance
            ));
        }
        if(binding.secondSelectExerciseText!!.text =="ACTIVE ASSISTED"){
            binding.secondAssistanceCard!!.visibility=View.VISIBLE
            binding.secondAssistanceText!!.text="Assistance"
            binding.secondAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                R.drawable.picture_assistance
            ));
        }

        if(binding.firstSelectedExerciseText!!.text =="AR ( ISOMETRIC )" || binding.secondSelectExerciseText!!.text =="AR ( ISOMETRIC )"){
            binding.holdTimeCard!!.visibility=View.VISIBLE
            if(binding.firstSelectedExerciseText!!.text =="AR ( ISOMETRIC )"){
                binding.firstAssistanceCard!!.visibility=View.VISIBLE
                binding.firstAssistanceText!!.text="Resistance"
                binding.firstAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
            if(binding.secondSelectExerciseText!!.text =="AR ( ISOMETRIC )"){
                binding.secondAssistanceCard!!.visibility=View.VISIBLE
                binding.secondAssistanceText!!.text="Resistance"
                binding.secondAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
        }

        if(binding.firstSelectedExerciseText!!.text =="AR ( ISOTONIC )" || binding.secondSelectExerciseText!!.text =="AR ( ISOTONIC )"){
            if(binding.firstSelectedExerciseText!!.text =="AR ( ISOTONIC )"){
                binding.firstAssistanceCard!!.visibility=View.VISIBLE
                binding.firstAssistanceText!!.text="Resistance"
                binding.firstAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
            if(binding.secondSelectExerciseText!!.text =="AR ( ISOTONIC )"){
                binding.secondAssistanceCard!!.visibility=View.VISIBLE
                binding.secondAssistanceText!!.text="Resistance"
                binding.secondAssistanceImage!!.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
        }
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

    fun clickListenerMovement(position: Int) {
        binding.dropdownMovement!!.visibility=View.GONE
        if(position==1) {
            binding.selectedMovementType!!.text = arr!![position]
        }else{
//            val view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)
//            common.comingSoonDialogBox(view,this)
            binding.selectedMovementType!!.text = arr!![position]
        }
    }

    fun onSessionClick(position: Int) {
        if(position == (sessionList.size-1)){
            showCreateSessionDialog()
        }else{
            myLiveData.setCurrentSession(  sessionList[position].deviceId.toInt())
            myLiveData.setCurrentSessionName(sessionList[position].deviceName)
            binding.selectedSessionText!!.text= myLiveData.currentSessionNameMutable.value
            startActivity(Intent(this,SessionInformation::class.java))
            finish()
        }
        binding.dropdownSessions!!.visibility = View.GONE
    }

    private fun showCreateSessionDialog() {
        val view = layoutInflater.inflate(R.layout.create_session_view,null)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val createButton = view.findViewById<CardView>(R.id.dialog_button_create)
        val cancelButton = view.findViewById<CardView>(R.id.dialog_button_cancel)
        val sessionName: TextInputEditText = view.findViewById<TextInputEditText>(R.id.session_name)
        builder.setView(view)
        createButton.setOnClickListener {
            createGuestSession(sessionName.text.toString())
            sessionList.clear()
            sessionAdder()
            builder.dismiss()
        }
        cancelButton.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun createGuestSession(name:String){
        val currentDateTime: String = sdf.format(Date())
        val currentDate = currentDateTime.split('_')
        mainViewModel.insertSession(Sessions(0,name,myLiveData.currentCaregiverMutable.value!!,myLiveData.currentPatientMutable.value!!,currentDate[0].toLong(),currentDate[1].toLong()))

    }

}