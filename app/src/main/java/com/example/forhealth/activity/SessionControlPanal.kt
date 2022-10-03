package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.MovementViewHolder
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.bluetooth.Bluetooth
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelExercise
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import kotlinx.android.synthetic.main.session_control_panal.*

class SessionControlPanal : AppCompatActivity() {

    private var common:Common?=null
    private var movementViewHolder: MovementViewHolder?=null
    private var myDatabaseHelper: MyDatabaseHelper?=null
    private var bluetooth:Bluetooth?=null
    private var view:View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.session_control_panal)
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        mContext = this
        viewOfNextMovement = layoutInflater.inflate(R.layout.want_to_start_next_movement,null)
        viewOfAllExercises = layoutInflater.inflate(R.layout.all_exercises_completed,null)

        hamburgerVisibilityManager = 1

        movementList!!.clear()
        data_view_card.visibility=View.VISIBLE

        common = Common(this)

        currentExerciseId = 9999

        session_name.text = selectedSessionName

        bluetooth = Bluetooth(this)
        view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)

        chart = findViewById(R.id.lineChart)
        chart.axisLeft.setDrawGridLines(false)
        xAxis = chart.xAxis
        yAxis = chart.axisLeft
        xAxis!!.setDrawGridLines(false)
        xAxis!!.setDrawAxisLine(false)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.animateX(1000, Easing.EaseInSine)
        xAxis!!.position = XAxis.XAxisPosition.BOTTOM
        xAxis!!.setDrawLabels(false)
        yAxis!!.setDrawLabels(true)
        xAxis!!.granularity = 1f

        bluetoothSetup()

        myDatabaseHelper = MyDatabaseHelper(this)
        movementViewHolder = MovementViewHolder(movementList,this)

        val movementRecyclerView = findViewById<RecyclerView>(R.id.movement_recycler_control_panal)
        movementRecyclerView.layoutManager = LinearLayoutManager(this@SessionControlPanal,
            LinearLayoutManager.HORIZONTAL,false)
        movementRecyclerView.adapter =  movementViewHolder

        back_button.setOnClickListener(View.OnClickListener {
            if(isClickable) {
                val iSessionInformation =
                    Intent(this@SessionControlPanal, SessionInformation::class.java)
                startActivity(iSessionInformation)
                finish()
            }else{
               alertStop( layoutInflater.inflate(R.layout.stop_alert,null))
            }
        })

        main_layout.setOnClickListener(View.OnClickListener {

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1

        })

        StaticReference.movement_no_session_control = movement_no_session_control

        StaticReference.given_exercise_type = given_exercise_type
        StaticReference.first_angle_session_control = first_angle_session_control
        StaticReference.second_angle_session_control = second_angle_session_control
        StaticReference.third_angle_session_control = third_angle_session_control
        StaticReference.given_repetition_session_control = given_repetition_session_control

        StaticReference.first_movement_type_session_control = first_movement_type_session_control
        StaticReference.second_movement_type_session_control = second_movement_type_session_control
        StaticReference.first_movement_speed_session_control = first_movement_speed_session_control
        StaticReference.second_movement_speed_session_control = second_movement_speed_session_control
        StaticReference.first_movement_assistance_session_control = first_movement_assistance_session_control
        StaticReference.second_movement_assistance_session_control = second_movement_assistance_session_control
        StaticReference.first_movement_hold_time_session_control = first_movement_hold_time_session_control
        StaticReference.second_movement_hold_time_session_control = second_movement_hold_time_session_control

        StaticReference.second_movement_assistance_session_control_card = second_movement_assistance_session_control_card
        StaticReference.second_hold_time_card = second_hold_time_card
        StaticReference.second_movement_parameter_assistance_and_resistsnce = second_movement_parameter_assistance_and_resistsnce

        StaticReference.first_movement_assistance_session_control_card = first_movement_assistance_session_control_card
        StaticReference.first_hold_time_card = first_hold_time_card
        StaticReference.first_movement_parameters_assistance_and_resistance = first_movement_parameters_assistance_and_resistance

        setMovements()
        buttonSetting()

    }

    private fun alertStop(view:View) {

            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
            val  yes = view.findViewById<CardView>(R.id.yes_button_stop)
            val  no = view.findViewById<CardView>(R.id.no_button_stop)
            if(view.parent != null){
                (view.parent as ViewGroup).removeView(view)
            }
            builder.setView(view)
            yes.setOnClickListener {
                if(Connection) {
                    bluetooth!!.sendMessage("0", this)
                }else{
                    Toast.makeText(this,"Device not Connected", Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(this@SessionControlPanal, SessionInformation::class.java))
                finish()
                builder.dismiss()
            }

            no.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(true)
            builder.show()

    }

    private fun setMovements() {
        cursorMovement = myDatabaseHelper!!.readDataByStringID(currentSessionId.toString(),"SESSION_ID_IN_EXERCISE","EXERCISES")
        var i = 1
        while (cursorMovement!!.moveToNext()){
            var list = cursorMovement!!.getString(4).split(",")
            var model = ModelExercise(1,"Movement $i",list[0],list[1],list[2],list[3],list[4],list[5],list[6],list[7],list[8],list[9],list[10],list[11],list[12],list[13],list[14],cursorMovement!!.getInt(0))
            movementList.add(model)
            i+=1
        }
        movementViewHolder!!.notifyDataSetChanged()

        setData()
    }

    fun onMovementClick(position: Int) {
        currentPosition = position

        setData()
    }

    fun setData() {
        try {
            cursorMovement!!.moveToPosition(currentPosition)
        }catch (e:Exception){

        }

        currentExerciseId = movementList[currentPosition].id

        StaticReference.movement_no_session_control.text = movementList[currentPosition].movementNo

        StaticReference.given_exercise_type.text = movementList[currentPosition].exerciseType
        StaticReference.first_angle_session_control.text = movementList[currentPosition].firstAngle
        StaticReference.second_angle_session_control.text = movementList[currentPosition].secondAngle
        StaticReference.third_angle_session_control.text = movementList[currentPosition].thirdAngle
        StaticReference.given_repetition_session_control.text = movementList[currentPosition].repetition

        StaticReference.first_movement_type_session_control.text = movementList[currentPosition].firstMovementType
        StaticReference.second_movement_type_session_control.text = movementList[currentPosition].secondMovementType
        StaticReference.first_movement_speed_session_control.text = movementList[currentPosition].firstMovementSpeed
        StaticReference.second_movement_speed_session_control.text = movementList[currentPosition].secondMovementSpeed
        StaticReference.first_movement_assistance_session_control.text = movementList[currentPosition].firstMovementAssistance
        StaticReference.second_movement_assistance_session_control.text = movementList[currentPosition].secondMovementAssistance
        StaticReference.first_movement_hold_time_session_control.text = movementList[currentPosition].firstMovementHoldTime
        StaticReference.second_movement_hold_time_session_control.text = movementList[currentPosition].secondMovementHoldTime

        if(movementList[currentPosition].firstMovementType == "PASSIVE"){
            StaticReference.first_movement_assistance_session_control_card.visibility=View.GONE
            StaticReference.first_hold_time_card.visibility=View.GONE
            StaticReference.first_movement_parameters_assistance_and_resistance.visibility=View.GONE
        }else{
            if(movementList[currentPosition].firstMovementType == "AR ( ISOMETRIC )"){
                StaticReference.second_movement_assistance_session_control_card.visibility=View.VISIBLE
                StaticReference.first_movement_parameters_assistance_and_resistance.visibility=View.VISIBLE
                StaticReference.first_movement_parameters_assistance_and_resistance.text = "Resistance"
                StaticReference.first_hold_time_card.visibility=View.VISIBLE
            }else{
                StaticReference.second_movement_assistance_session_control_card.visibility=View.VISIBLE
                StaticReference.first_movement_parameters_assistance_and_resistance.visibility=View.VISIBLE
                StaticReference.first_movement_parameters_assistance_and_resistance.text = "Assistance"
                StaticReference.first_hold_time_card.visibility=View.GONE
            }
        }

        if(movementList[currentPosition].secondMovementType == "PASSIVE"){
            StaticReference.second_movement_assistance_session_control_card.visibility=View.GONE
            StaticReference.second_hold_time_card.visibility=View.GONE
            StaticReference.second_movement_parameter_assistance_and_resistsnce.visibility=View.GONE
        }else{
            if(movementList[currentPosition].secondMovementType == "AR ( ISOMETRIC )"){
                StaticReference.second_movement_assistance_session_control_card.visibility=View.VISIBLE
                StaticReference.second_movement_parameter_assistance_and_resistsnce.visibility=View.VISIBLE
                StaticReference.second_movement_parameter_assistance_and_resistsnce.text = "Resistance"
                StaticReference.second_hold_time_card.visibility=View.VISIBLE
            }else{
                StaticReference.second_movement_assistance_session_control_card.visibility=View.VISIBLE
                StaticReference.second_movement_parameter_assistance_and_resistsnce.visibility=View.VISIBLE
                StaticReference.second_movement_parameter_assistance_and_resistsnce.text = "Assistance"
                StaticReference.second_hold_time_card.visibility=View.GONE
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun buttonSetting(){

        playpause.setOnClickListener(View.OnClickListener {
            if(isClickable) {
                controls.visibility = View.GONE
                if (Connection) {
                    controls.visibility = View.VISIBLE
                    bluetooth!!.sendMessage("1," + cursorMovement!!.getString(4), this)
                    Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show()
                } else {

                    controls.visibility = View.GONE

                    mBtAdapter = BluetoothAdapter.getDefaultAdapter()
                    if (hamburgerVisibilityManager == 1) {
                        if (!mBtAdapter.isEnabled) {
                            var bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            ContextCompat.startActivity(this, bluetoothIntent, null)
                            common!!.findPairedDevices()
                        } else {
                            common!!.findPairedDevices()
                        }
                        sidebar.visibility = View.VISIBLE
                        all_controls_card.visibility = View.GONE
                        hamburgerVisibilityManager = 0
                    } else {
                        sidebar.visibility = View.GONE
                        all_controls_card.visibility = View.GONE
                        hamburgerVisibilityManager = 1
                    }
                }
            }else{
                Toast.makeText(this,"Already is use",Toast.LENGTH_SHORT).show()
            }
        })

        stopsession.setOnClickListener(View.OnClickListener {
            if(Connection) {
                bluetooth!!.sendMessage("0", this)
            }else{
                Toast.makeText(this,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        resetsession.setOnClickListener(View.OnClickListener {
            if(Connection) {
                common!!.comingSoonDialogBox(view!!)
            }else{
                Toast.makeText(this,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        preview.setOnClickListener(View.OnClickListener {
            common!!.comingSoonDialogBox(view!!)
        })
    }

    private fun bluetoothSetup() {

        aSwitch = control_brake_state
        torque = control_torque
        angle = control_encoder_I
        speed = control_encoder_II

        chart = lineChart
        StaticReference.speedMeter = speedMeter
        currentRepetition = repetition_completed_session_control

        feedBack = feedback
        angleValue = angle_value
        speedValue = speed_value
        torqueValue = torque_value

        inputPageConnection = 2

        val common = Common(this)
        common.setClickForSideBar(hamburger,controls,sidebar,all_controls_card)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,sidebar,all_controls_card,controls,control_shutdown,control_reset,control_set_home,control_close,control_direction_clockwise,control_direction_anticlockwise,control_brake_state,control_refresh)

        pairedDevicesViewHolder = PairedDevicesViewHolder(pairedDevicesList,this,progress_bar,sidebar,controls)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = pairedDevicesViewHolder
    }



}