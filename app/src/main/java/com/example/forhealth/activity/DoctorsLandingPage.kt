package com.example.forhealth.activity

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.ScheduledSessionViewHolder
import com.example.forhealth.database.*
import com.example.forhealth.datamodel.ModelScheduledSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.doctors_landing_page.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.example.forhealth.dagger.*
import com.example.forhealth.databinding.DoctorsLandingPageBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import java.io.File
import java.io.FileWriter
import kotlin.collections.HashMap


class DoctorsLandingPage : AppCompatActivity() {

    private var scheduledSessionList = ArrayList<ModelScheduledSession>()
    private val scheduledSessionsAdapter =  ScheduledSessionViewHolder(scheduledSessionList,this)
    private var db = FirebaseFirestore.getInstance()
    private lateinit var binding : DoctorsLandingPageBinding
    @Inject
    @CommonQualifier lateinit var common:Services

    @Inject
    @BluetoothQualifier lateinit var bluetooth:Services

    lateinit var mainViewModel: DatabaseViewModel

    @Inject
    lateinit var myDatabaseInstance: MyDatabaseInstance

    lateinit var myLiveData:MyLiveData

    var check = false

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this,R.layout.doctors_landing_page)

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        // hide bottom navigation bar

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        val dao = myDatabaseInstance.databaseDao()
        val repository = DataRepository(dao)



        myLiveData = MyLiveData.getMyLiveData(dao,bluetooth)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(DatabaseViewModel::class.java)

        myLiveData.liveDataMutable.observe(this, Observer {
                binding.tourqe =it[0]+" nm"
                binding.angle =it[1]+" deg"
                binding.speed =it[2]+" rpm"
//                bluetooth.sendMessage("send")

        })

        myLiveData.btConnectionMutable.observe(this, Observer {

            if(myLiveData.btConnectionMutable.value==true) {
                check = true
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                binding.controls.visibility = View.VISIBLE
            }else{
                if(check) {
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                    check = false
                    binding.controls.visibility = View.GONE
                    binding.allControlsCard.visibility = View.GONE
                }
            }
        })

        myLiveData.setCurrentPatient(9999)
        myLiveData.setCurrentSession(9999)
        myLiveData.setCurrentExercise(9999)

        getDataFromDatabase()
        calenderSettings()
        bluetoothSetup()

        binding.backToSplashScreen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@DoctorsLandingPage, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        binding.logoutButton.setOnClickListener(View.OnClickListener {
            myLiveData.setCurrentCaregiver(9999)
            val iExistingUsers = Intent(this@DoctorsLandingPage, ExistingUsers::class.java)
            startActivity(iExistingUsers)
            finish()
        })

        binding.existingPatientButton.setOnClickListener(View.OnClickListener {
            val iExistingPatient = Intent(this@DoctorsLandingPage, ExistingPatient::class.java)
            startActivity(iExistingPatient)
            finish()
        })

        binding.newPatient.setOnClickListener(View.OnClickListener {
            val iCreateNewPatient = Intent(this@DoctorsLandingPage, CreateNewPatient::class.java)
            startActivity(iCreateNewPatient)
            finish()
        })

        binding.startGuestSession.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@DoctorsLandingPage, GuestMode::class.java))
            finish()
        })

        binding.mainLayout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            if(binding.sidebar.visibility == View.VISIBLE || binding.allControlsCard.visibility == View.VISIBLE) {
                binding.sidebar.visibility = View.GONE
                binding.allControlsCard.visibility = View.GONE
            }
        })

        binding.downloadButton.setOnClickListener(View.OnClickListener {
            exportDB()
        })

        binding.uploadButton.setOnClickListener(View.OnClickListener {

          sendDataToCloud()
        })
    }

    private fun sendDataToCloud() {

        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_data_save,null)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        builder.setView(view)
        builder.setCanceledOnTouchOutside(false)
        builder.show()
        val sTimeout = 1000

        var firstCollection = db.collection("Doctors").document(myLiveData.doctorEmailMutable.value!!)
        var secondCollection = db.collection("Doctors").document(myLiveData.doctorEmailMutable.value!!).collection("Patients")


        mainViewModel.getCaregiverById(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
            if(!it.isNullOrEmpty()){
                for (i in it) {
                    val dataOfDoctors = hashMapOf(
                        "Name" to i.caregiverName,
                        "Email" to i.caregiverEmail
                    )

                    mainViewModel.getPatientByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this,
                        Observer {

                            if(!it.isNullOrEmpty()){
                                for (i in it) {
                                    val dataOfPatient = hashMapOf(
                                        "Name" to i.patientName,
                                        "Contact" to i.patientContact.toString(),
                                        "Date" to i.patientDate.toString()
                                    )

                                    addToCloud(firstCollection,secondCollection,dataOfDoctors,dataOfPatient,i)
                                }


                                Handler().postDelayed({
                                    builder.dismiss()
                                    Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()

                                },sTimeout.toLong())
                                Log.e("CSV", "Writing Done!")
                        }else{
                                Handler().postDelayed({
                                    builder.dismiss()
                                    Toast.makeText(this,"Failed!",Toast.LENGTH_SHORT).show()

                                },sTimeout.toLong())
                                Log.e("CSV", "Writing Failed!")
                            }

                    })
                }

            }else{
                Handler().postDelayed({
                    builder.dismiss()
                    Toast.makeText(this,"Failed!",Toast.LENGTH_SHORT).show()

                },sTimeout.toLong())
                Log.e("CSV", "Writing Failed!")
            }
        })




    }

    private fun addToCloud(
        firstCollection: DocumentReference,
        secondCollection: CollectionReference,
        dataOfDoctors: HashMap<String, String>,
        dataOfPatient: HashMap<String, String>,
        i:Patients
    ) {
        firstCollection.set(dataOfDoctors)
            .addOnSuccessListener {
                secondCollection.document(i.patientName).set(dataOfPatient)
                    .addOnSuccessListener {
                        Log.e(ContentValues.TAG,
                            "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.e(ContentValues.TAG,
                            "Error writing document",
                            e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(
                    ContentValues.TAG, "Error writing document", e)
            }

        Log.e("CLOUD", "Writing...")
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
        mainViewModel.getCaregiverById(myLiveData.currentCaregiverMutable.value!!).observe(this,
            Observer {
                binding.doctorNameText = it[0].caregiverName
                if(it[0].caregiverAvatar == 0){
                    binding.doctorProfilePicture.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.avatar_male
                        ));
                }
                else{
                    if(it[0].caregiverAvatar == 1){
                        binding.doctorProfilePicture.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.avatar_female_a
                            ));
                    }else{
                        binding.doctorProfilePicture.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.avatar_female_b
                            ));
                    }
                }
            })

        sessionRecycler()

        mainViewModel.getSessionByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this,
            Observer {
                if (!it.isNullOrEmpty()){
                    scheduledSessionList.clear()
                    for (i in it!!) {
                        scheduledSessionList.add(ModelScheduledSession(i.sessionName,i.sessionId))
                    }
                    scheduledSessionsAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun sessionRecycler(){
        val existingUsersRecyclerView = findViewById<RecyclerView>(R.id.scheduled_session_recycler)
        existingUsersRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        existingUsersRecyclerView.adapter = scheduledSessionsAdapter
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
        common.setClickForSideBar(binding.hamburger,binding.controls,binding.sidebar,binding.allControlsCard,this)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,binding.sidebar,binding.allControlsCard,binding.controls,binding.controlShutdown,binding.controlReset,binding.controlSetHome,binding.controlClose,binding.controlDirectionClockwise,binding.controlDirectionAnticlockwise,binding.controlBrakeState,binding.controlRefresh,this,binding.speedSlider)
        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = myLiveData.pairedDevicesViewHolder

        binding.speedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.speedValue!!.text= value.toInt().toString()+" %"
        }

    }

    fun onItemClick(position: Int) {

    }

    private fun askForPermission(){
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getPermission = Intent()
                getPermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getPermission)
            }else{

                val view = layoutInflater.inflate(R.layout.custom_dialog_layout_data_save,null)
                val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
                builder.setView(view)
                builder.setCanceledOnTouchOutside(false)
                builder.show()

                val exportDir = File(Environment.getExternalStorageDirectory(), "Forhealth")
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }
                val fileCaregiver = File(exportDir, "for_health_caregiver_table_${myLiveData.doctorEmailMutable.value!!}.csv")
                val filePatient = File(exportDir, "for_health_patient_table_${myLiveData.doctorEmailMutable.value!!}.csv")
                val fileSession = File(exportDir, "for_health_session_table_${myLiveData.doctorEmailMutable.value!!}.csv")
                val fileExercise = File(exportDir, "for_health_exercise_table_${myLiveData.doctorEmailMutable.value!!}.csv")
                val fileData = File(exportDir, "for_health_data_table_${myLiveData.doctorEmailMutable.value!!}.csv")

                try {
                    fileCaregiver.createNewFile()
                    filePatient.createNewFile()
                    fileSession.createNewFile()
                    fileExercise.createNewFile()
                    fileData.createNewFile()

                    mainViewModel.getCaregiverById(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
                        if(!it.isNullOrEmpty()){
                            val csvWrite = CSVWriter(FileWriter(fileCaregiver))
                            val arrStr = arrayOf("CAREGIVER ID",
                                "CAREGIVER NAME",
                                "CAREGIVER EMAIL",
                                "CAREGIVER AVATAR",
                                "CAREGIVER PIN")
                            csvWrite.writeNext(arrStr)
                            for (i in it) {
                                val arrStr = arrayOf(i.caregiverId.toString(),
                                    i.caregiverName,
                                    i.caregiverEmail,
                                    i.caregiverAvatar.toString(),
                                    "XXXX")
                                csvWrite.writeNext(arrStr)
                                Log.e("CSV", "Writing...")
                            }
                            csvWrite.close()
                        }
                    })

                    mainViewModel.getPatientByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
                        if(!it.isNullOrEmpty()){
                            val csvWrite = CSVWriter(FileWriter(filePatient))
                            val arrStr = arrayOf(
                                "PATIENT ID",
                                "PATIENT AVATAR",
                                "PATIENT NAME",
                                "PATIENT AGE",
                                "PATIENT SEX",
                                "PATIENT WEIGHT" ,
                                "PATIENT CONTACT",
                                "DATE")
                            csvWrite.writeNext(arrStr)
                            for (i in it) {
                                val arrStr = arrayOf(
                                    i.patientId.toString(),
                                    i.patientAvatar.toString(),
                                    i.patientName,
                                    i.patientAge.toString(),
                                    i.patientSex,
                                    i.patientWeight.toString() ,
                                    i.patientContact.toString(),
                                    i.patientDate.toString())
                                csvWrite.writeNext(arrStr)
                                Log.e("CSV", "Writing...")
                            }
                            csvWrite.close()
                        }
                    })

                    mainViewModel.getSessionByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
                        if(!it.isNullOrEmpty()){
                            val csvWrite = CSVWriter(FileWriter(fileSession))
                            val arrStr = arrayOf(
                                "SESSION ID",
                                "CAREGIVER ID IN SESSION",
                                "PATIENT ID IN SESSION",
                                "SESSION NAME",
                                "DATE" ,
                                "TIME")
                            csvWrite.writeNext(arrStr)
                            for (i in it) {
                                val arrStr = arrayOf(
                                    i.sessionId.toString(),
                                    i.caregiverIdInSession.toString(),
                                    i.patientIdInSession.toString(),
                                    i.sessionName,
                                    i.sessionDate.toString() ,
                                    i.sessionTime.toString())
                                csvWrite.writeNext(arrStr)
                                Log.e("CSV", "Writing...")
                            }
                            csvWrite.close()
                        }
                    })

                    mainViewModel.getExerciseByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
                        if(!it.isNullOrEmpty()){
                            val csvWrite = CSVWriter(FileWriter(fileExercise))
                            val arrStr = arrayOf(
                                "EXERCISE ID",
                                "CAREGIVER ID IN EXERCISE",
                                "PATIENT ID IN EXERCISE",
                                "SESSION ID IN EXERCISE",
                                "EXERCISE PARAMETERS" ,
                                "DATE",
                                "TIME")
                            csvWrite.writeNext(arrStr)
                            for (i in it) {
                                val arrStr = arrayOf(
                                    i.ExerciseId.toString(),
                                    i.caregiverIdInExercise.toString(),
                                    i.patientIdInExercise.toString(),
                                    i.sessionIdInExercise.toString(),
                                    i.exerciseParameters ,
                                    i.exerciseDate.toString(),
                                    i.exerciseTime.toString())
                                csvWrite.writeNext(arrStr)
                                Log.e("CSV", "Writing...")
                            }
                            csvWrite.close()
                        }
                    })

                    mainViewModel.getDataByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this, Observer {
                        if(!it.isNullOrEmpty()){
                            val csvWrite = CSVWriter(FileWriter(fileData))
                            val arrStr = arrayOf(
                                "DATA ID",
                                "CAREGIVER ID IN DATA",
                                "PATIENT ID IN DATA",
                                "EXERCISE ID IN DATA",
                                "EXERCISE DATA",
                                "DATE",
                                "TIME")
                            csvWrite.writeNext(arrStr)
                            for (i in it) {
                                val arrStr = arrayOf(
                                    i.dataId.toString(),
                                    i.caregiverIdInData.toString(),
                                    i.patientIdInData.toString(),
                                    i.exerciseIdInData.toString(),
                                    i.exerciseData ,
                                    i.exerciseDate.toString(),
                                    i.exerciseTime.toString())
                                csvWrite.writeNext(arrStr)
                                Log.e("CSV", "Writing...")
                            }
                            csvWrite.close()
                        }
                    })

                    val sTimeout = 1000
                    Handler().postDelayed({
                        builder.dismiss()
                        Toast.makeText(this,"Done!",Toast.LENGTH_SHORT).show()

                    },sTimeout.toLong())
                    Log.e("CSV", "Writing Done!")

                } catch (sqlEx: Exception) {
                    Log.e("CSV", sqlEx.message, sqlEx)
                }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun exportDB() {
        if(isExternalStorageWritable()) {
            askForPermission()
        }else{
            askForPermission()
            Log.e("CSV", "Need permission")
        }
    }


}