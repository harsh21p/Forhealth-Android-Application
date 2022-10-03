package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import kotlinx.android.synthetic.main.create_new_patient.*
import kotlinx.android.synthetic.main.create_new_patient.all_controls_card
import kotlinx.android.synthetic.main.create_new_patient.back_button
import kotlinx.android.synthetic.main.create_new_patient.control_brake_state
import kotlinx.android.synthetic.main.create_new_patient.control_close
import kotlinx.android.synthetic.main.create_new_patient.control_direction_anticlockwise
import kotlinx.android.synthetic.main.create_new_patient.control_direction_clockwise
import kotlinx.android.synthetic.main.create_new_patient.control_encoder_I
import kotlinx.android.synthetic.main.create_new_patient.control_encoder_II
import kotlinx.android.synthetic.main.create_new_patient.control_refresh
import kotlinx.android.synthetic.main.create_new_patient.control_reset
import kotlinx.android.synthetic.main.create_new_patient.control_set_home
import kotlinx.android.synthetic.main.create_new_patient.control_shutdown
import kotlinx.android.synthetic.main.create_new_patient.control_torque
import kotlinx.android.synthetic.main.create_new_patient.controls
import kotlinx.android.synthetic.main.create_new_patient.hamburger
import kotlinx.android.synthetic.main.create_new_patient.main_layout
import kotlinx.android.synthetic.main.create_new_patient.patient_age
import kotlinx.android.synthetic.main.create_new_patient.patient_contact
import kotlinx.android.synthetic.main.create_new_patient.patient_name
import kotlinx.android.synthetic.main.create_new_patient.patient_weight
import kotlinx.android.synthetic.main.create_new_patient.progress_bar
import kotlinx.android.synthetic.main.create_new_patient.sidebar
import kotlinx.android.synthetic.main.patient_profile_page.*
import java.text.SimpleDateFormat
import java.util.*

class CreateNewPatient : AppCompatActivity() {

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var myDatabaseHelper:MyDatabaseHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.create_new_patient)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        hamburgerVisibilityManager = 1
        speedMeter = null
        chart = null

        val common =Common(this)

        holder_profile.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.patient_avatar_layout,null)
            common.avatarDialogBoxForPatient(patient_profile,view)
        })

        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this@CreateNewPatient, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })
        myDatabaseHelper = MyDatabaseHelper(this)

        save_button.setOnClickListener(View.OnClickListener {
            addData()
        })

        main_layout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard()

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1
        })

        bluetoothSetup()
    }

    private fun addData() {
        if(patient_name.text!!.isBlank() || patient_age.text!!.isBlank() || patient_contact.text!!.isBlank()  || patient_weight.text!!.isBlank() || patient_gender.text!!.isBlank() ){
            Toast.makeText(this,"All field's are required",Toast.LENGTH_SHORT).show()
        }else{
            val currentDateTime: String = sdf.format(Date())
            val currentDate = currentDateTime.split('_')
            var  result = myDatabaseHelper!!.addDataToPatients(selectedPatientAvatar.toString(),mAuthString.toString(),patient_weight.text.toString(),patient_age.text.toString(),patient_name.text!!.toString(),patient_gender.text.toString(),currentDate[0],patient_contact.text.toString())
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show()
            val iExistingPatient= Intent(this@CreateNewPatient, ExistingPatient::class.java)
            startActivity(iExistingPatient)
            finish()
        }
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
}