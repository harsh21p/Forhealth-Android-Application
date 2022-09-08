package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.adapter.ScheduledSessionViewHolder
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelScheduledSession
import kotlinx.android.synthetic.main.doctors_landing_page.*
import kotlinx.android.synthetic.main.doctors_landing_page.all_controls_card
import kotlinx.android.synthetic.main.doctors_landing_page.control_brake_state
import kotlinx.android.synthetic.main.doctors_landing_page.control_close
import kotlinx.android.synthetic.main.doctors_landing_page.control_direction_anticlockwise
import kotlinx.android.synthetic.main.doctors_landing_page.control_direction_clockwise
import kotlinx.android.synthetic.main.doctors_landing_page.control_encoder_I
import kotlinx.android.synthetic.main.doctors_landing_page.control_encoder_II
import kotlinx.android.synthetic.main.doctors_landing_page.control_refresh
import kotlinx.android.synthetic.main.doctors_landing_page.control_reset
import kotlinx.android.synthetic.main.doctors_landing_page.control_set_home
import kotlinx.android.synthetic.main.doctors_landing_page.control_shutdown
import kotlinx.android.synthetic.main.doctors_landing_page.control_torque
import kotlinx.android.synthetic.main.doctors_landing_page.controls
import kotlinx.android.synthetic.main.doctors_landing_page.hamburger
import kotlinx.android.synthetic.main.doctors_landing_page.progress_bar
import kotlinx.android.synthetic.main.doctors_landing_page.sidebar
import java.text.SimpleDateFormat
import java.util.*

class DoctorsLandingPage : AppCompatActivity() {
    private var mMyDatabaseHelper: MyDatabaseHelper?=null
    private var scheduledSessionList = ArrayList<ModelScheduledSession>()
    private val scheduledSessionsAdapter =  ScheduledSessionViewHolder(scheduledSessionList,this)
    private val common = Common(this)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide notification bsr

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.doctors_landing_page)

        // hide bottom navigation bar

        selectedSessionName = null
        currentSessionId = 9999
        selectedPatientId = 9999
        speedMeter = null
        chart = null
        hamburgerVisibilityManager = 1

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        getDataFromDatabase()
        sessionRecycler()
        calenderSettings()

        bluetoothSetup()


        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@DoctorsLandingPage, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        logout_button.setOnClickListener(View.OnClickListener {
            mAuthString=9999
            val iExistingUsers = Intent(this@DoctorsLandingPage, ExistingUsers::class.java)
            startActivity(iExistingUsers)
            finish()
        })

        existing_patient_button.setOnClickListener(View.OnClickListener {
            val iExistingPatient = Intent(this@DoctorsLandingPage, ExistingPatient::class.java)
            startActivity(iExistingPatient)
            finish()
        })

        new_patient.setOnClickListener(View.OnClickListener {
            val iCreateNewPatient = Intent(this@DoctorsLandingPage, CreateNewPatient::class.java)
            startActivity(iCreateNewPatient)
            finish()
        })

        start_guest_session.setOnClickListener(View.OnClickListener {
            val iGuestMode = Intent(this@DoctorsLandingPage, GuestMode::class.java)
            startActivity(iGuestMode)
            finish()
        })

        main_layout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard()
            if(sidebar.visibility == View.VISIBLE || all_controls_card.visibility == View.VISIBLE) {
                sidebar.visibility = View.GONE
                all_controls_card.visibility = View.GONE
                hamburgerVisibilityManager = 1
            }
        })
    }

    fun getSqlToExcel(){
//        val sqliteToExcel = SqliteToExcel(this, "helloworld.db")
    }

    private fun calenderSettings() {
        var date:String?=null
        var monthWords: String?=null
        var monthAndYear: String?=null
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val selectedDate: String = sdf.format(Date(calendarView.date))
        val listOfDate = selectedDate.split('/')

        date = if(listOfDate[1].length == 1){
            "0"+listOfDate[1]
        }else{
            listOfDate[1]
        }
        monthAndYear = findMonth(listOfDate[0].toInt())
        monthAndYear = monthAndYear+"\n"+listOfDate[2]
        today_date.text = date
        month_and_year.text = monthAndYear

        calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            date = if (dayOfMonth.toString().length == 1) {
                "0$dayOfMonth"
            } else {
                "$dayOfMonth"
            }
            monthWords = findMonth(month+1)
            monthAndYear = "$monthWords\n$year"
            today_date.text = date
            month_and_year.text = monthAndYear
        })
    }

    private fun getDataFromDatabase() {
        mMyDatabaseHelper= MyDatabaseHelper(this)
        var cursor = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID","CAREGIVERS")
        cursor.moveToFirst()

        try {
            doctor_name.text="Dr. "+cursor.getString(1)
            if(cursor.getString(4).toInt()==0)
            {
                doctor_profile_picture.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.avatar_male
                    ));
            }else{
                if(cursor.getString(4).toInt()==1)
                {
                    doctor_profile_picture.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.avatar_female_a
                        ));
                }else{
                    doctor_profile_picture.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.avatar_female_b
                        ));
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun sessionRecycler(){
        val existingUsersRecyclerView = findViewById<RecyclerView>(R.id.scheduled_session_recycler)
        existingUsersRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        existingUsersRecyclerView.adapter = scheduledSessionsAdapter

        var cursor = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID_IN_SESSIONS","SESSIONS")
        while (cursor.moveToNext()){
            var model = ModelScheduledSession(cursor.getString(3),cursor.getString(0).toInt())
            scheduledSessionList.add(model)
        }
        scheduledSessionsAdapter.notifyDataSetChanged()
    }


    private fun findMonth(month: Int): String? {
        var monthInString:String?=null
        when(month){

            1 -> monthInString="January"
            2 -> monthInString="February"
            3 -> monthInString="March"
            4 -> monthInString="April"
            5 -> monthInString="May"
            6 -> monthInString="June"
            7 -> monthInString="July"
            8 -> monthInString="August"
            9 -> monthInString="September"
            10 -> monthInString="October"
            11 -> monthInString="November"
            12 -> monthInString="December"
        }
        return monthInString
    }


    private fun bluetoothSetup() {

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

    }


    fun onItemClick(position: Int) {

    }
}