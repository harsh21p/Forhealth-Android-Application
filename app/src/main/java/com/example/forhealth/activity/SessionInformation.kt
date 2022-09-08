package com.example.forhealth.activity

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
import com.example.forhealth.adapter.ExerciseViewHolder
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelExercise
import kotlinx.android.synthetic.main.sessons_information.*
import kotlinx.android.synthetic.main.sessons_information.all_controls_card
import kotlinx.android.synthetic.main.sessons_information.back_button
import kotlinx.android.synthetic.main.sessons_information.control_brake_state
import kotlinx.android.synthetic.main.sessons_information.control_close
import kotlinx.android.synthetic.main.sessons_information.control_direction_anticlockwise
import kotlinx.android.synthetic.main.sessons_information.control_direction_clockwise
import kotlinx.android.synthetic.main.sessons_information.control_encoder_I
import kotlinx.android.synthetic.main.sessons_information.control_encoder_II
import kotlinx.android.synthetic.main.sessons_information.control_refresh
import kotlinx.android.synthetic.main.sessons_information.control_reset
import kotlinx.android.synthetic.main.sessons_information.control_set_home
import kotlinx.android.synthetic.main.sessons_information.control_shutdown
import kotlinx.android.synthetic.main.sessons_information.control_torque
import kotlinx.android.synthetic.main.sessons_information.controls
import kotlinx.android.synthetic.main.sessons_information.hamburger
import kotlinx.android.synthetic.main.sessons_information.progress_bar
import kotlinx.android.synthetic.main.sessons_information.sidebar
import java.util.ArrayList

class SessionInformation: AppCompatActivity() {

    private var myDatabaseHelper:MyDatabaseHelper?=null
    private var movementViewHolder: ExerciseViewHolder?=null
    private var exerciseList = ArrayList<ModelExercise>()
    private var cursor:Cursor? = null
    private var exist = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.sessons_information)
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        hamburgerVisibilityManager = 1
        speedMeter = null
        chart = null

        bluetoothSetup()

        back_button.setOnClickListener(View.OnClickListener {
            val iGuestModePage = Intent(this@SessionInformation, GuestMode::class.java)
            startActivity(iGuestModePage)
            finish()
        })

        play_session.setOnClickListener(View.OnClickListener {

            if(exist) {
                val iSessionControlPanal =
                    Intent(this@SessionInformation, SessionControlPanal::class.java)
                startActivity(iSessionControlPanal)
                finish()
            }else{
                Toast.makeText(this,"Movement not found",Toast.LENGTH_SHORT).show()
            }

        })

        main_layout.setOnClickListener(View.OnClickListener {

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1

        })

        myDatabaseHelper = MyDatabaseHelper(this)
        movementViewHolder = ExerciseViewHolder(exerciseList,this)

        val movementRecyclerView = findViewById<RecyclerView>(R.id.movement_recycler)
        movementRecyclerView.layoutManager = LinearLayoutManager(this@SessionInformation,
            LinearLayoutManager.HORIZONTAL,false)
        movementRecyclerView.adapter =  movementViewHolder

        addMovement()

    }

    fun addMovement(){
        cursor = myDatabaseHelper!!.readDataByStringID(currentSessionId.toString(),"SESSION_ID_IN_EXERCISE","EXERCISES")
        var i = 1
        while (cursor!!.moveToNext()){
            exist = true
            var list = cursor!!.getString(4).split(",")
            var model = ModelExercise(1,"Movement $i",list[0],list[1],list[2],list[3],list[4],list[5],list[6],list[7],list[8],list[9],list[10],list[11],list[12],list[13],list[14],cursor!!.getInt(0))
            exerciseList.add(model)
            i+=1
        }
        var model = ModelExercise(2,"","","","","","","","","","","","","","","","",0)
        exerciseList.add(model)
        movementViewHolder!!.notifyDataSetChanged()
    }

    private fun bluetoothSetup() {

        aSwitch = control_brake_state
        torque = control_torque
        angle = control_encoder_I
        speed = control_encoder_II

        inputPageConnection = 1
        val common = Common(this)
        common.setClickForSideBar(hamburger,controls,sidebar,all_controls_card)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,sidebar,all_controls_card,controls,control_shutdown,control_reset,control_set_home,control_close,control_direction_clockwise,control_direction_anticlockwise,control_brake_state,control_refresh)

        pairedDevicesViewHolder = PairedDevicesViewHolder(pairedDevicesList,this,progress_bar,sidebar,controls)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = pairedDevicesViewHolder

    }

    fun onExeciseClick(position: Int) {
        if((exerciseList.size -1 ) == position){
            val iGuestModePage = Intent(this@SessionInformation, GuestMode::class.java)
            startActivity(iGuestModePage)
            finish()
        }else{

        }
    }
}