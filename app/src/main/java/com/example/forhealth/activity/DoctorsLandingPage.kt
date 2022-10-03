package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.PairedDevicesViewHolder
import com.example.forhealth.adapter.ScheduledSessionViewHolder
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.common.Common
import com.example.forhealth.database.CSVWriter
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelScheduledSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.doctors_landing_page.*
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class DoctorsLandingPage : AppCompatActivity() {
    private var mMyDatabaseHelper: MyDatabaseHelper?=null
    private var scheduledSessionList = ArrayList<ModelScheduledSession>()
    private val scheduledSessionsAdapter =  ScheduledSessionViewHolder(scheduledSessionList,this)
    private val common = Common(this)
    private var doctorEmail:String?=null
    private var db = FirebaseFirestore.getInstance()

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

        download_button.setOnClickListener(View.OnClickListener {
            exportDB()
        })

        upload_button.setOnClickListener(View.OnClickListener {


            val dataOfDoctors = hashMapOf(
                "Text" to "",
                "Status" to ""
            )
            val dataOfPatient = hashMapOf(
                "Name" to "",
                "Year" to "",
                "Enrollment" to ""
            )

            var firstCollection = db.collection("Doctors").document(doctorEmail!!)
            var secondCollection = db.collection("Doctors").document(doctorEmail!!).collection("Patients").document()

            firstCollection.set(dataOfDoctors)
                .addOnSuccessListener {

                    secondCollection.set(dataOfPatient)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error writing document", e)
                        }
                }
                .addOnFailureListener { e -> Log.w(
                    ContentValues.TAG, "Error writing document", e) }
        })
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
            doctorEmail = cursor.getString(3)
            doctor_name.text = cursor.getString(1)
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

    private fun exportDB() {
        if(isExternalStorageWritable()) {
            askForPermission()

            val view = layoutInflater.inflate(R.layout.custom_dialog_layout_data_save,null)
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
            builder.setView(view)
            builder.setCanceledOnTouchOutside(false)
            builder.show()

            val exportDir = File(Environment.getExternalStorageDirectory(), "Forhealth")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            val file1 = File(exportDir, "forhealth_data_table_$doctorEmail.csv")
            val file2 = File(exportDir, "forhealth_exercises_table_$doctorEmail.csv")
            val file3 = File(exportDir, "forhealth_sessions_table_$doctorEmail.csv")
            val file4 = File(exportDir, "forhealth_patients_table_$doctorEmail.csv")
            val file5 = File(exportDir, "forhealth_caregivers_table_$doctorEmail.csv")
            try {
                file1.createNewFile()
                val csvWrite1 = CSVWriter(FileWriter(file1))
                val csvWrite2 = CSVWriter(FileWriter(file2))
                val csvWrite3 = CSVWriter(FileWriter(file3))
                val csvWrite4 = CSVWriter(FileWriter(file4))
                val csvWrite5 = CSVWriter(FileWriter(file5))


                val curCSV1 = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID_IN_DATA","DATA")
                val curCSV2 = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID_IN_EXERCISE","EXERCISES")
                val curCSV3 = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID_IN_SESSIONS","SESSIONS")
                val curCSV4 = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID_IN_PATIENT","PATIENTS")
                val curCSV5 = mMyDatabaseHelper!!.readDataByIntID(mAuthString,"CAREGIVER_ID","CAREGIVERS")

                csvWrite1.writeNext(curCSV1.columnNames)
                while (curCSV1.moveToNext()) {
                    val arrStr = arrayOf(curCSV1.getString(0),
                        curCSV1.getString(1),
                        curCSV1.getString(2),
                        curCSV1.getString(3),
                        curCSV1.getString(4),
                        curCSV1.getString(5),
                        curCSV1.getString(6),
                        curCSV1.getString(7))
                    csvWrite1.writeNext(arrStr)
                    Log.e("CSV", "Writing...")
                }

                csvWrite1.close()
                curCSV1.close()

                csvWrite2.writeNext(curCSV2.columnNames)
                while (curCSV2.moveToNext()) {
                    val arrStr = arrayOf(curCSV2.getString(0),
                        curCSV2.getString(1),
                        curCSV2.getString(2),
                        curCSV2.getString(3),
                        curCSV2.getString(4),
                        curCSV2.getString(5),
                        curCSV2.getString(6),
                        curCSV2.getString(7))
                    csvWrite2.writeNext(arrStr)
                    Log.e("CSV", "Writing...")
                }


                csvWrite2.close()
                curCSV2.close()

                csvWrite3.writeNext(curCSV3.columnNames)
                while (curCSV3.moveToNext()) {
                    val arrStr = arrayOf(curCSV3.getString(0),
                        curCSV3.getString(1),
                        curCSV3.getString(2),
                        curCSV3.getString(3),
                        curCSV3.getString(4),
                        curCSV3.getString(5),
                        curCSV3.getString(6))
                    csvWrite3.writeNext(arrStr)
                    Log.e("CSV", "Writing...")
                }

                csvWrite3.close()
                curCSV3.close()

                csvWrite4.writeNext(curCSV4.columnNames)
                while (curCSV4.moveToNext()) {
                    val arrStr = arrayOf(curCSV4.getString(0),
                        curCSV4.getString(1),
                        curCSV4.getString(2),
                        curCSV4.getString(3),
                        curCSV4.getString(4),
                        curCSV4.getString(5),
                        curCSV4.getString(6),
                        curCSV4.getString(7),
                        curCSV4.getString(8))
                    csvWrite4.writeNext(arrStr)
                    Log.e("CSV", "Writing...")
                }

                csvWrite4.close()
                curCSV4.close()

                csvWrite5.writeNext(curCSV5.columnNames)
                while (curCSV5.moveToNext()) {
                    val arrStr = arrayOf(curCSV5.getString(0),
                        curCSV5.getString(1),
                        "XXXX",
                        curCSV5.getString(3),
                        curCSV5.getString(4))
                    csvWrite5.writeNext(arrStr)
                    Log.e("CSV", "Writing...")
                }

                csvWrite5.close()
                curCSV5.close()

                val sTimeout = 1000
                Handler().postDelayed({
                    builder.dismiss()
                    Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()

                },sTimeout.toLong())
                Log.e("CSV", "Writing Done!")

            } catch (sqlEx: Exception) {
                Log.e("CSV", sqlEx.message, sqlEx)
            }
        }else{
            askForPermission()
            Log.e("CSV", "Need permission")
        }
    }

    private fun askForPermission(){
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getPermission = Intent()
                getPermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getPermission)
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

}