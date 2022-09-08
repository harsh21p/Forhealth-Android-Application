package com.example.forhealth.activity

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.adapter.SessionsViewHolder
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelScheduledSession
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.patient_profile_page.*
import kotlinx.android.synthetic.main.patient_profile_page.all_controls_card
import kotlinx.android.synthetic.main.patient_profile_page.back_button
import kotlinx.android.synthetic.main.patient_profile_page.control_brake_state
import kotlinx.android.synthetic.main.patient_profile_page.control_close
import kotlinx.android.synthetic.main.patient_profile_page.control_direction_anticlockwise
import kotlinx.android.synthetic.main.patient_profile_page.control_direction_clockwise
import kotlinx.android.synthetic.main.patient_profile_page.control_encoder_I
import kotlinx.android.synthetic.main.patient_profile_page.control_encoder_II
import kotlinx.android.synthetic.main.patient_profile_page.control_refresh
import kotlinx.android.synthetic.main.patient_profile_page.control_reset
import kotlinx.android.synthetic.main.patient_profile_page.control_set_home
import kotlinx.android.synthetic.main.patient_profile_page.control_shutdown
import kotlinx.android.synthetic.main.patient_profile_page.control_torque
import kotlinx.android.synthetic.main.patient_profile_page.controls
import kotlinx.android.synthetic.main.patient_profile_page.hamburger
import kotlinx.android.synthetic.main.patient_profile_page.patient_name
import kotlinx.android.synthetic.main.patient_profile_page.progress_bar
import kotlinx.android.synthetic.main.patient_profile_page.sidebar
import java.text.SimpleDateFormat
import java.util.*

class PatientProfilePage : AppCompatActivity() {

    private var myDatabaseHelper: MyDatabaseHelper?=null
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var sessionList = ArrayList<ModelScheduledSession>()
    private val sessionsAdapter =  SessionsViewHolder(sessionList,this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.patient_profile_page)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        hamburgerVisibilityManager = 1

        selectedSessionName = null
        currentSessionId = 9999
        speedMeter = null
        chart = null

        myDatabaseHelper = MyDatabaseHelper(this)
        bluetoothSetup()
        getData()

        val sessionRecyclerView = findViewById<RecyclerView>(R.id.session_list_recycler)
        sessionRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        sessionRecyclerView.adapter = sessionsAdapter


        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this@PatientProfilePage, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })

        main_view.visibility=View.VISIBLE

        create_session_button.setOnClickListener(View.OnClickListener {

            showCreateSessionDilog()

        })
        existing_session_button.setOnClickListener(View.OnClickListener {
            sessionView()
        })

        close_button.setOnClickListener(View.OnClickListener {
            session_list.visibility=View.GONE
            main_view.visibility=View.VISIBLE
        })

        main_layout.setOnClickListener(View.OnClickListener {

            sidebar.visibility = View.GONE
            all_controls_card.visibility = View.GONE
            hamburgerVisibilityManager = 1
        })
    }

    private fun showCreateSessionDilog() {
        val view = layoutInflater.inflate(R.layout.create_session_view,null)
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
            val createButton = view.findViewById<CardView>(R.id.dialog_button_create)
            val cancelButton = view.findViewById<CardView>(R.id.dialog_button_cancel)
            val sessionName:TextInputEditText = view.findViewById<TextInputEditText>(R.id.session_name)
            builder.setView(view)
        createButton.setOnClickListener {
            val currentDateTime: String = sdf.format(Date())
            val currentDate = currentDateTime.split('_')
            myDatabaseHelper!!.addDataToSessions(mAuthString.toString(),selectedPatientId.toString(),
                sessionName.text.toString(),currentDate[0],currentDate[1],"1")
                sessionView()
                builder.dismiss()

            }
        cancelButton.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun sessionView() {
        sessionList.clear()
        var cursor = myDatabaseHelper!!.readSessionByPatientId("1", mAuthString.toString(), selectedPatientId.toString())

        while (cursor.moveToNext()){
            var model = ModelScheduledSession(cursor.getString(3),cursor.getString(0).toInt())
            sessionList.add(model)
        }
        sessionsAdapter.notifyDataSetChanged()
        session_list.visibility=View.VISIBLE
        main_view.visibility=View.GONE

    }

    private fun getData() {

        var cursor = myDatabaseHelper!!.readDataByIntID(selectedPatientId,"PATIENT_ID","PATIENTS")
        cursor.moveToFirst()
        setData(cursor)

    }

    private fun setData(cursor: Cursor) {
        if(cursor.getString(3).toInt() == 0){
            profile_patient.setImageDrawable(
                ContextCompat.getDrawable(this,
                    R.drawable.male_avatar_patient_a
                ));
        }else{
            if(cursor.getString(3).toInt() == 1){
                profile_patient.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.male_avatar_patient_b
                    ));
            }else{
                if(cursor.getString(3).toInt() == 2){
                    profile_patient.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.female_avatar_patient_a
                        ));
                }else{
                    if(cursor.getString(3).toInt() == 3){
                        profile_patient.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.female_avatar_patient_b
                            ));
                    }
                }
            }
        }

        patient_name.text = cursor.getString(2)
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

    fun onSessionClick(position:Int) {
        selectedSessionName = sessionList[position].sessionName
        currentSessionId = sessionList[position].sessionId
        val iGuestMode = Intent(this@PatientProfilePage, SessionInformation::class.java)
        startActivity(iGuestMode)
        finish()
    }
}