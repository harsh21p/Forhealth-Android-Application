package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.ExistingPatientViewHolder
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelExistingPatient
import kotlinx.android.synthetic.main.existing_patient.*
import kotlinx.android.synthetic.main.existing_patient.all_controls_card
import kotlinx.android.synthetic.main.existing_patient.back_button
import kotlinx.android.synthetic.main.existing_patient.control_brake_state
import kotlinx.android.synthetic.main.existing_patient.control_close
import kotlinx.android.synthetic.main.existing_patient.control_direction_anticlockwise
import kotlinx.android.synthetic.main.existing_patient.control_direction_clockwise
import kotlinx.android.synthetic.main.existing_patient.control_encoder_I
import kotlinx.android.synthetic.main.existing_patient.control_encoder_II
import kotlinx.android.synthetic.main.existing_patient.control_refresh
import kotlinx.android.synthetic.main.existing_patient.control_reset
import kotlinx.android.synthetic.main.existing_patient.control_set_home
import kotlinx.android.synthetic.main.existing_patient.control_shutdown
import kotlinx.android.synthetic.main.existing_patient.control_torque
import kotlinx.android.synthetic.main.existing_patient.controls
import kotlinx.android.synthetic.main.existing_patient.hamburger
import kotlinx.android.synthetic.main.existing_patient.progress_bar
import kotlinx.android.synthetic.main.existing_patient.sidebar
import java.util.ArrayList

class ExistingPatient: AppCompatActivity() {
    private var mMyDatabaseHelper: MyDatabaseHelper?=null
    private var existingPatientList = ArrayList<ModelExistingPatient>()
    private val existingPatientAdapter =  ExistingPatientViewHolder(existingPatientList,this)

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide notification bsr

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.existing_patient)

        mMyDatabaseHelper = MyDatabaseHelper(this)

        selectedPatientId = 9999
        selectedSessionName = null
        currentSessionId = 9999
        speedMeter = null
        chart = null

        // hide bottom navigation bar

       hamburgerVisibilityManager = 1

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this@ExistingPatient, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })



        main_layout.setOnClickListener(View.OnClickListener {

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1
        })

        bluetoothSetup()
        patientRecycler()
    }

    private fun patientRecycler(){
        val existingPatientRecyclerView = findViewById<RecyclerView>(R.id.recycler_patient)
        existingPatientRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        existingPatientRecyclerView.adapter = existingPatientAdapter

        var cursor = mMyDatabaseHelper!!.readDataByIntID(StaticReference.mAuthString,"CAREGIVER_ID_IN_PATIENT","PATIENTS")
        while (cursor.moveToNext() && cursor.getString(3)!=null){
            var model = ModelExistingPatient(cursor.getString(3).toInt(),cursor.getString(2),cursor.getString(5).toInt(),cursor.getString(7),cursor.getString(8),cursor.getInt(0))
            existingPatientList.add(model)
            existingPatientAdapter.notifyDataSetChanged()
        }

    }

    private fun bluetoothSetup() {

        StaticReference.aSwitch = control_brake_state
        StaticReference.torque = control_torque
        StaticReference.angle = control_encoder_I
        StaticReference.speed = control_encoder_II

        StaticReference.inputPageConnection = 1
        val common = Common(this)
        common.setClickForSideBar(hamburger,controls,sidebar,all_controls_card)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,sidebar,all_controls_card,controls,control_shutdown,control_reset,control_set_home,control_close,control_direction_clockwise,control_direction_anticlockwise,control_brake_state,control_refresh)

        StaticReference.pairedDevicesViewHolder = PairedDevicesViewHolder(StaticReference.pairedDevicesList,this,progress_bar,sidebar,controls)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = StaticReference.pairedDevicesViewHolder

    }

    fun onClickPatient(position:Int) {
        selectedPatientId = existingPatientList[position].id
        val iPatientProfilePage = Intent(this@ExistingPatient, PatientProfilePage::class.java)
        startActivity(iPatientProfilePage)
        finish()
    }
}