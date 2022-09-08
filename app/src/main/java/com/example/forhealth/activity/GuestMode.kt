package com.example.forhealth.activity

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.Time
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.DropdownMovementViewHolder
import com.example.forhealth.adapter.DropdownSessionViewHolder
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelMovement
import kotlinx.android.synthetic.main.dropdown_movement_view.*
import kotlinx.android.synthetic.main.guest_mode.*
import kotlinx.android.synthetic.main.movement_view_layout.*
import java.text.SimpleDateFormat
import java.util.*

class GuestMode: AppCompatActivity() {

    private var dropdownExercisesVisiblity = false
    private var firstSelectExerciseButton = false
    private var secondSelectExerciseButton = false
    private var dropdownMovementVisiblity = false
    private var myDatabaseHelper:MyDatabaseHelper?=null
    private var firstAssistance = 0
    private var secondAssistance = 0
    private var dropdownMovementViewHolder:DropdownMovementViewHolder?=null
    private var dropdownSessionViewHolder: DropdownSessionViewHolder?=null
    private var movementList = ArrayList<ModelMovement>()
    private var sessionList = ArrayList<ModelMovement>()
    private var arr: Array<String>? = null
    private val common = Common(this)
    private var sessionNo = 1

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.guest_mode)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions


        speedMeter = null
        chart = null

        hamburgerVisibilityManager = 1;


         arr = arrayOf("HIP", "KNEE", "ANKLE")
        var i = 0
        while (i< arr!!.size){
            var movement = ModelMovement(arr!![i])
            movementList.add(movement)
            i++
        }

        myDatabaseHelper = MyDatabaseHelper(this)

        aSwitch = control_brake_state
        torque = control_torque
        angle = control_encoder_I
        speed = control_encoder_II

        inputPageConnection = 1

        common.setClickForSideBar(hamburger,controls,sidebar,all_controls_card)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,sidebar,all_controls_card,controls,control_shutdown,control_reset,control_set_home,control_close,control_direction_clockwise,control_direction_anticlockwise,control_brake_state,control_refresh)

        pairedDevicesViewHolder = PairedDevicesViewHolder(pairedDevicesList,this,progress_bar,sidebar,controls)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = pairedDevicesViewHolder


        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this@GuestMode, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })

        view_movement_button.setOnClickListener(View.OnClickListener {
            val iSessionInformationPage = Intent(this@GuestMode, SessionInformation::class.java)
            startActivity(iSessionInformationPage)
            finish()
        })

        dropdown_select_session_button.setOnClickListener(View.OnClickListener {
            if(sessionList.isNotEmpty()) {
                dropdown_sessions.visibility = View.VISIBLE
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

        main_layout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard()
                sidebar.visibility = View.GONE
                all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1

                dropdown_exercises.visibility = View.GONE
                dropdown_movement.visibility = View.GONE
                dropdown_sessions.visibility=View.GONE

                dropdownExercisesVisiblity = false
                firstSelectExerciseButton = false
                secondSelectExerciseButton = false
                dropdownMovementVisiblity = false
        })

        first_select_exercise_button.setOnClickListener(View.OnClickListener {
            common.hideKeyboard()
            dropdown_exercises.visibility=View.VISIBLE
            dropdownExercisesVisiblity = true
            firstSelectExerciseButton = true
            secondSelectExerciseButton = false
        })

        second_select_exercise_button.setOnClickListener(View.OnClickListener {
            common.hideKeyboard()
            dropdown_exercises.visibility=View.VISIBLE
            dropdownExercisesVisiblity = true
            secondSelectExerciseButton = true
            firstSelectExerciseButton = false
        })

        passive_button.setOnClickListener(View.OnClickListener {
            dropdown_exercises.visibility=View.GONE
            if(firstSelectExerciseButton){
                first_selected_exercise_text.text="PASSIVE"
                firstAssistance = 0
            }
            else{
                if(secondSelectExerciseButton) {
                    second_select_exercise_text.text = "PASSIVE"
                    secondAssistance = 0
                }
            }
            exerciseVisibilityManager()
        })

        active_assisted_button.setOnClickListener(View.OnClickListener {
            dropdown_exercises.visibility=View.GONE
            if(firstSelectExerciseButton){
                first_selected_exercise_text.text="ACTIVE ASSISTED"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    second_select_exercise_text.text="ACTIVE ASSISTED"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })
        active_resisted_isometric_button.setOnClickListener(View.OnClickListener {
            dropdown_exercises.visibility=View.GONE
            if(firstSelectExerciseButton){
                first_selected_exercise_text.text="AR ( ISOMETRIC )"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    second_select_exercise_text.text="AR ( ISOMETRIC )"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })
        dropdown_choose_movement_button.setOnClickListener(View.OnClickListener {
            if(dropdownMovementVisiblity) {
                dropdown_movement.visibility = View.GONE
                dropdownMovementVisiblity = false
            }else{
                dropdown_movement.visibility = View.VISIBLE
                dropdownMovementVisiblity = true
            }
        })
        none_button.setOnClickListener(View.OnClickListener {
            dropdown_exercises.visibility=View.GONE
            if(firstSelectExerciseButton){
                first_selected_exercise_text.text="None"
                firstAssistance = 1
            }
            else{
                if(secondSelectExerciseButton)
                    second_select_exercise_text.text="None"
                secondAssistance = 1
            }
            exerciseVisibilityManager()
        })

        given_angle_two.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                given_angle_three.text = given_angle_two.text
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                given_angle_three.text = given_angle_two.text
            }
        })

        first_assistance_slider.addOnChangeListener { slider, value, fromUser ->
            first_assistance_value.text= value.toInt().toString()+" %"
            firstAssistance = value.toInt()
        }

        second_assistance_slider.addOnChangeListener { slider, value, fromUser ->
            second_assistance_value.text= value.toInt().toString()+" %"
            secondAssistance = value.toInt()
        }

        first_speed_slider.addOnChangeListener { slider, value, fromUser ->
            first_speed_value.text= value.toInt().toString()+" %"
        }

        second_speed_slider.addOnChangeListener { slider, value, fromUser ->
            second_speed_slider_value.text= value.toInt().toString()+" %"
        }

        save_button.setOnClickListener(View.OnClickListener {
            addData()
        })
    }


    private fun exerciseVisibilityManager() {
        hold_time_card.visibility=View.GONE
        first_assistance_card.visibility=View.GONE
        second_assistance_card.visibility=View.GONE

        if(first_selected_exercise_text.text =="ACTIVE ASSISTED"){
            first_assistance_card.visibility=View.VISIBLE
            first_assistance_text.text="Assistance"
            first_assistance_image.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                R.drawable.picture_assistance
            ));
        }
        if(second_select_exercise_text.text =="ACTIVE ASSISTED"){
            second_assistance_card.visibility=View.VISIBLE
            second_assistance_text.text="Assistance"
            second_assistance_image.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                R.drawable.picture_assistance
            ));
        }

        if(first_selected_exercise_text.text =="AR ( ISOMETRIC )" || second_select_exercise_text.text =="AR ( ISOMETRIC )"){
            hold_time_card.visibility=View.VISIBLE
            if(first_selected_exercise_text.text =="AR ( ISOMETRIC )"){
                first_assistance_card.visibility=View.VISIBLE
                first_assistance_text.text="Resistance"
                first_assistance_image.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
            if(second_select_exercise_text.text =="AR ( ISOMETRIC )"){
                second_assistance_card.visibility=View.VISIBLE
                second_assistance_text.text="Resistance"
                second_assistance_image.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.picture_resistance
                ));
            }
        }
    }

    private fun sessionAdder() {
        var cursor:Cursor?=null
        if(selectedPatientId != 9999){
            selected_session_text.text= selectedSessionName
             cursor = myDatabaseHelper!!.readDataByStringID("0","IDENTIFIER","SESSIONS")
        }else{
            cursor = myDatabaseHelper!!.readSessionByPatientId("1","IDENTIFIER", selectedPatientId.toString())
        }
        while (cursor.moveToNext()){
            var session = ModelMovement(cursor!!.getString(3))
            movementList.add(session)
            sessionNo +=1
        }

        dropdownSessionViewHolder!!.notifyDataSetChanged()
    }

    private fun addData(){

        if(first_selected_exercise_text.text == "None" || second_select_exercise_text.text == "None" ){
            Toast.makeText(this,"Exercise type not selected",Toast.LENGTH_SHORT).show()
        }else {
            if (given_repetitions.text!!.isBlank()) {
                Toast.makeText(this, "Repetitions cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                if (given_angle_one.text!!.isBlank() || given_angle_two.text!!.isBlank() || given_angle_three.text!!.isBlank() || given_angle_four.text!!.isBlank()) {
                    Toast.makeText(this, "Angle cannot be empty", Toast.LENGTH_SHORT).show()
                } else {

                    val currentDateTime: String = sdf.format(Date())
                    val currentDate = currentDateTime.split('_')
                    val data: String = getMove()

                    if(selectedPatientId != 9999){
                        try {
                            var result  = myDatabaseHelper!!.addDataToExercises(mAuthString.toString(),
                                selectedPatientId.toString(),
                                currentSessionId.toString(),
                                data,
                                currentDate[0],
                                currentDate[1],
                                "1")

                            if(result=="Successful"){
                                val iSessionInformationPage = Intent(this@GuestMode, SessionInformation::class.java)
                                startActivity(iSessionInformationPage)
                                finish()
                            }else{
                                Toast.makeText(this,result,Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        try {
                            myDatabaseHelper!!.addDataToExercises(mAuthString.toString(),
                                "9999",
                                "9999",
                                data,
                                currentDate[0],
                                currentDate[1],
                                "0")
                        } catch (e: Exception) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    private fun getMove(): String {
        return selected_movement_type.text.toString() + "," + given_angle_one.text.toString()+","+given_angle_two.text.toString()+","+given_angle_four.text.toString()+","+given_repetitions.text.toString()+","+first_speed_slider.value.toInt().toString()+","+first_assistance_slider.value.toInt().toString()+","+first_assistance_slider.value.toInt().toString()+","+given_hold_time.text.toString()+","+second_speed_slider.value.toInt().toString()+","+second_assistance_slider.value.toInt().toString()+","+second_assistance_slider.value.toInt().toString()+","+given_hold_time.text.toString()+","+first_selected_exercise_text.text.toString()+","+second_select_exercise_text.text.toString()
    }

    fun clickListenerMovement(position:Int) {
        dropdown_movement.visibility=View.GONE
        if(position==1) {
            selected_movement_type.text = arr!![position]
        }else{
            val view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)
            common.comingSoonDialogBox(view)
        }
    }

    fun onSessionClick(position: Int) {

    }

}