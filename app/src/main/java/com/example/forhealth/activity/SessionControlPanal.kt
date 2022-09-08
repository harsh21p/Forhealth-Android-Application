package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.session_control_panal.*
import kotlinx.android.synthetic.main.session_control_panal.all_controls_card
import kotlinx.android.synthetic.main.session_control_panal.back_button
import kotlinx.android.synthetic.main.session_control_panal.control_brake_state
import kotlinx.android.synthetic.main.session_control_panal.control_close
import kotlinx.android.synthetic.main.session_control_panal.control_direction_anticlockwise
import kotlinx.android.synthetic.main.session_control_panal.control_direction_clockwise
import kotlinx.android.synthetic.main.session_control_panal.control_encoder_I
import kotlinx.android.synthetic.main.session_control_panal.control_encoder_II
import kotlinx.android.synthetic.main.session_control_panal.control_refresh
import kotlinx.android.synthetic.main.session_control_panal.control_reset
import kotlinx.android.synthetic.main.session_control_panal.control_set_home
import kotlinx.android.synthetic.main.session_control_panal.control_shutdown
import kotlinx.android.synthetic.main.session_control_panal.control_torque
import kotlinx.android.synthetic.main.session_control_panal.controls
import kotlinx.android.synthetic.main.session_control_panal.hamburger
import kotlinx.android.synthetic.main.session_control_panal.progress_bar
import kotlinx.android.synthetic.main.session_control_panal.sidebar
import java.util.ArrayList

class SessionControlPanal : AppCompatActivity() {

    private var common:Common?=null
    private var movementViewHolder: MovementViewHolder?=null
    private var movementList = ArrayList<ModelExercise>()
    private var myDatabaseHelper: MyDatabaseHelper?=null
    private var currentPosition:Int = 0
    private var cursor:Cursor?=null
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

        hamburgerVisibilityManager = 1

        data_view_card.visibility=View.VISIBLE

        common = Common(this)

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
            val iSessionInformation = Intent(this@SessionControlPanal, SessionInformation::class.java)
            startActivity(iSessionInformation)
            finish()
        })

        main_layout.setOnClickListener(View.OnClickListener {

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1

        })

        setMovements()
        buttonSetting()
    }

    private fun setMovements() {
        cursor = myDatabaseHelper!!.readDataByStringID(currentSessionId.toString(),"SESSION_ID_IN_EXERCISE","EXERCISES")
        var i = 1
        while (cursor!!.moveToNext()){
            var list = cursor!!.getString(4).split(",")
            var model = ModelExercise(1,"Movement $i",list[0],list[1],list[2],list[3],list[4],list[5],list[6],list[7],list[8],list[9],list[10],list[11],list[12],list[13],list[14],cursor!!.getInt(0))
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

    private fun setData() {
        try {
            cursor!!.moveToPosition(currentPosition)
        }catch (e:Exception){

        }
        currentExerciseId = movementList[currentPosition].id

        movement_no_session_control.text = movementList[currentPosition].movementNo

        given_exercise_type.text = movementList[currentPosition].exerciseType
        first_angle_session_control.text = movementList[currentPosition].firstAngle
        second_angle_session_control.text = movementList[currentPosition].secondAngle
        third_angle_session_control.text = movementList[currentPosition].thirdAngle
        given_repetition_session_control.text = movementList[currentPosition].repetition

        first_movement_type_session_control.text = movementList[currentPosition].firstMovementType
        second_movement_type_session_control.text = movementList[currentPosition].secondMovementType
        first_movement_speed_session_control.text = movementList[currentPosition].firstMovementSpeed
        second_movement_speed_session_control.text = movementList[currentPosition].secondMovementSpeed
        first_movement_assistance_session_control.text = movementList[currentPosition].firstMovementAssistance
        second_movement_assistance_session_control.text = movementList[currentPosition].secondMovementAssistance
        first_movement_hold_time_session_control.text = movementList[currentPosition].firstMovementHoldTime
        second_movement_hold_time_session_control.text = movementList[currentPosition].secondMovementHoldTime


        if(movementList[currentPosition].firstMovementType == "PASSIVE"){
            first_movement_assistance_session_control_card.visibility=View.GONE
            first_hold_time_card.visibility=View.GONE
            first_movement_parameters_assistance_and_resistance.visibility=View.GONE
        }else{
            if(movementList[currentPosition].firstMovementType == "AR ( ISOMETRIC )"){
                second_movement_assistance_session_control_card.visibility=View.VISIBLE
                first_movement_parameters_assistance_and_resistance.visibility=View.VISIBLE
                first_movement_parameters_assistance_and_resistance.text = "Resistance"
                first_hold_time_card.visibility=View.VISIBLE
            }else{
                second_movement_assistance_session_control_card.visibility=View.VISIBLE
                first_movement_parameters_assistance_and_resistance.visibility=View.VISIBLE
                first_movement_parameters_assistance_and_resistance.text = "Assistance"
                first_hold_time_card.visibility=View.GONE
            }
        }

        if(movementList[currentPosition].secondMovementType == "PASSIVE"){
            second_movement_assistance_session_control_card.visibility=View.GONE
            second_hold_time_card.visibility=View.GONE
            second_movement_parameter_assistance_and_resistsnce.visibility=View.GONE
        }else{
            if(movementList[currentPosition].secondMovementType == "AR ( ISOMETRIC )"){
                second_movement_assistance_session_control_card.visibility=View.VISIBLE
                second_movement_parameter_assistance_and_resistsnce.visibility=View.VISIBLE
                second_movement_parameter_assistance_and_resistsnce.text = "Resistance"
                second_hold_time_card.visibility=View.VISIBLE
            }else{
                second_movement_assistance_session_control_card.visibility=View.VISIBLE
                second_movement_parameter_assistance_and_resistsnce.visibility=View.VISIBLE
                second_movement_parameter_assistance_and_resistsnce.text = "Assistance"
                second_hold_time_card.visibility=View.GONE
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun buttonSetting(){

        playpause.setOnClickListener(View.OnClickListener {
            controls.visibility=View.GONE
            if(Connection){
                controls.visibility = View.VISIBLE
                bluetooth!!.sendMessage("1,"+cursor!!.getString(4),this)
                Toast.makeText(this,"Processing...",Toast.LENGTH_SHORT).show()
                playpause.isClickable=false
            }else {
                if (hamburgerVisibilityManager == 1) {
                    if (!mBtAdapter.isEnabled) {
                        sidebar.visibility=View.GONE
                        hamburgerVisibilityManager = 1
                        var bluetoothintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivity(bluetoothintent)
                    } else {
                        sidebar.visibility = View.VISIBLE
                        hamburgerVisibilityManager = 0
                    }
                    all_controls_card.visibility = View.GONE
                } else {
                    sidebar.visibility = View.GONE
                    all_controls_card.visibility = View.GONE
                    hamburgerVisibilityManager = 1
                }
            }
        })

        stopsession.setOnClickListener(View.OnClickListener {
            if(Connection) {
                bluetooth!!.sendMessage("0", this)
                playpause.isClickable = true
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