package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.SessionsViewHolder
import com.example.forhealth.dagger.*
import com.example.forhealth.database.*
import com.example.forhealth.databinding.PatientProfilePageBinding
import com.example.forhealth.datamodel.ModelExistingPatient
import com.example.forhealth.datamodel.ModelScheduledSession
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.patient_profile_page.*
import kotlinx.android.synthetic.main.text_box_popup.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PatientProfilePage : AppCompatActivity() {

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    private var sessionList = ArrayList<ModelScheduledSession>()
    private val sessionsAdapter =  SessionsViewHolder(sessionList,this)

    @Inject
    @CommonQualifier
    lateinit var common : Services

    @Inject
    @BluetoothQualifier
    lateinit var bluetooth : Services

    var check = false

    @Inject
    lateinit var myDatabaseInstance : MyDatabaseInstance

    lateinit var mainViewModel : DatabaseViewModel

    lateinit var myLiveData : MyLiveData

    private lateinit var binding : PatientProfilePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this,R.layout.patient_profile_page)

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        val dao = myDatabaseInstance.databaseDao()
        val repository = DataRepository(dao)

        myLiveData = MyLiveData.getMyLiveData(dao,bluetooth)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(
            DatabaseViewModel::class.java)


        myLiveData.liveDataMutable.observe(this, androidx.lifecycle.Observer {
            binding.tourqe = it[0]+" nm"
            binding.angle = it[1]+" deg"
            binding.speed = it[2]+" rpm"
//            bluetooth.sendMessage("send")

        })
        myLiveData.btConnectionMutable.observe(this, androidx.lifecycle.Observer {
            if(myLiveData.btConnectionMutable.value==true) {
                check = true
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                binding.controls!!.visibility = View.VISIBLE
            }else{
                if(check) {
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                    check = false
                    binding.controls!!.visibility = View.GONE
                    binding.allControlsCard!!.visibility = View.GONE
                }
            }
        })

        binding.backButton!!.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this, ExistingPatient::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })


        binding.deleteButton!!.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.custom_dialog_layout_delete,null)
            dialogBoxDelete(view,9999)
        })

        binding.mainLayout!!.setOnClickListener(View.OnClickListener {
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
        })

        bluetoothSetup()

        getData()

        val sessionRecyclerView = findViewById<RecyclerView>(R.id.session_list_recycler)
        sessionRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        sessionRecyclerView.adapter = sessionsAdapter

        binding.mainView!!.visibility=View.VISIBLE

        binding.createSessionButton!!.setOnClickListener(View.OnClickListener {

            showCreateSessionDilog()

        })

        binding.closeButton!!.setOnClickListener(View.OnClickListener {
            binding.sessionList!!.visibility=View.GONE
            binding.mainView!!.visibility=View.VISIBLE
        })


        binding.mainLayout.setOnClickListener(View.OnClickListener {
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
        })

        binding.existingSessionButton!!.setOnClickListener(View.OnClickListener {
            sessionView()
        })

        uploadReport()

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
            try {

                mainViewModel.insertSession(Sessions(0,
                    sessionName.text.toString(),
                    myLiveData.currentCaregiverMutable.value!!,
                    myLiveData.currentPatientMutable.value!!,
                    currentDate[0].toLong(),
                    currentDate[1].toLong()))

                Toast.makeText(this,"New session created",Toast.LENGTH_SHORT).show()

            }catch (e:Exception){
                Log.e("Error session insertion",e.toString())
                Toast.makeText(this,"Failed to add session",Toast.LENGTH_SHORT).show()
            }
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
        mainViewModel.getSessionByPatient(myLiveData.currentPatientMutable.value!!).observe(this,
            androidx.lifecycle.Observer {
                if(!it.isNullOrEmpty()){
                    sessionList.clear()
                    for (i in it!!) {
                        sessionList.add(ModelScheduledSession(i.sessionName,i.sessionId))
                    }
                    sessionsAdapter.notifyDataSetChanged()
                }
            })

        sessionsAdapter.notifyDataSetChanged()
        binding.sessionList!!.visibility=View.VISIBLE
        binding.mainView!!.visibility=View.GONE

    }


    private fun getData() {

        mainViewModel.getPatientById(myLiveData.currentPatientMutable.value!!).observe(this, androidx.lifecycle.Observer {
            if (!it.isNullOrEmpty()){
                if(it[0].patientAvatar == 0) {
                    binding.profilePatient!!.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.male_avatar_patient_a
                        ));
                }else{
                    if(it[0].patientAvatar==1){
                        binding.profilePatient!!.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.male_avatar_patient_b
                            ));
                    }else{
                        if(it[0].patientAvatar==2){
                            binding.profilePatient!!.setImageDrawable(
                                ContextCompat.getDrawable(this,
                                    R.drawable.female_avatar_patient_a
                                ));
                        }else{
                            binding.profilePatient!!.setImageDrawable(
                                ContextCompat.getDrawable(this,
                                    R.drawable.female_avatar_patient_b
                                ));
                        }
                    }
                }
                binding.patientName!!.text = it[0].patientName
                binding.patientAge!!.text = it[0].patientAge.toString()
                binding.patientContact!!.text = it[0].patientContact.toString()
                binding.patientWeight!!.text = it[0].patientWeight.toString()

            }else{
                Log.e("Data","Data not found")
            }
        })


    }

    private fun bluetoothSetup() {
        common.setClickForSideBar(binding.hamburger!!,binding.controls!!,binding.sidebar!!,binding.allControlsCard!!,this)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,binding.sidebar!!,binding.allControlsCard!!,binding.controls!!,binding.controlShutdown!!,binding.controlReset!!,binding.controlSetHome!!,binding.controlClose!!,binding.controlDirectionClockwise!!,binding.controlDirectionAnticlockwise!!,binding.controlBrakeState!!,binding.controlRefresh!!,this,binding.speedSlider!!)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = myLiveData.pairedDevicesViewHolder

        binding.speedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.speedValue!!.text= value.toInt().toString()+" %"
        }
    }

    fun onSessionClick(position: Int) {
        myLiveData.setCurrentSession(sessionList[position].sessionId)
        startActivity(Intent(this@PatientProfilePage, SessionInformation::class.java))
        finish()
    }

    fun deleteButton(position: Int) {
        myLiveData.setCurrentSession(sessionList[position].sessionId)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_delete,null)
        dialogBoxDelete(view,position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dialogBoxDelete(view: View, position: Int) {

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.delete_dialog_yes_button)
        val  no = view.findViewById<CardView>(R.id.delete_dialog_cancel_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            if(position != 9999) {
                mainViewModel.deleteDataBySession(myLiveData.currentSessionMutable.value!!)
                mainViewModel.deleteExerciseBySession(myLiveData.currentSessionMutable.value!!)
                mainViewModel.deleteSession(myLiveData.currentSessionMutable.value!!)
                sessionList.removeAt(position)
                sessionsAdapter.notifyDataSetChanged()
            }else{
                mainViewModel.deleteDataByPatient(myLiveData.currentPatientMutable.value!!)
                mainViewModel.deleteExerciseByPatient(myLiveData.currentPatientMutable.value!!)
                mainViewModel.deleteSessionByPatient(myLiveData.currentPatientMutable.value!!)
                mainViewModel.deletePatient(myLiveData.currentPatientMutable.value!!)
                startActivity(Intent(this, ExistingPatient::class.java))
                finish()
            }
            builder.dismiss()
        }
        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun uploadReport(){
        val view = layoutInflater.inflate(R.layout.report_popup,null)
        binding.reportUploadButton!!.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
            val type = view.findViewById<CardView>(R.id.type_text)
            val file = view.findViewById<CardView>(R.id.upload)
            val photo = view.findViewById<CardView>(R.id.take_photo)
            if(view.parent != null){
                (view.parent as ViewGroup).removeView(view)
            }
            builder.setView(view)

            type.setOnClickListener(View.OnClickListener {

                builder.dismiss()

                val view1 = layoutInflater.inflate(R.layout.text_box_popup,null)
                val builder1 = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
                val save = view1.findViewById<CardView>(R.id.make_pdf)
                val edit : EditText = view1.findViewById<EditText>(R.id.text_for_pdf)
                val progress : ProgressBar = view1.findViewById<ProgressBar>(R.id.progressBar)
                val textOfButton = view1.findViewById<TextView>(R.id.text_of_button)
                val viewPdf = view1.findViewById<CardView>(R.id.view_button)

                if(view1.parent != null){
                    (view1.parent as ViewGroup).removeView(view1)
                }
                builder1.setView(view1)

                viewPdf.setOnClickListener(View.OnClickListener {
                    builder1.dismiss()

                    viewReportPdf()

                })

                save.setOnClickListener(View.OnClickListener {
                    // make pdf
                    textOfButton.visibility = View.INVISIBLE
                    progress.visibility = View.VISIBLE
                    val text = edit.text.toString()
                    saveAsPdf(text)
                    textOfButton.visibility = View.VISIBLE
                    progress.visibility = View.INVISIBLE
                    builder1.dismiss()
                })

                builder1.setCanceledOnTouchOutside(true)
                builder1.show()
            })
            file.setOnClickListener(View.OnClickListener {
                selectFileAndSaveAsPdf()
                builder.dismiss()
            })
            photo.setOnClickListener(View.OnClickListener {
                openCameraAndSavePhotoAsPdf()
                builder.dismiss()
            })

            builder.setCanceledOnTouchOutside(true)
            builder.show()
        })
    }

    private fun selectFileAndSaveAsPdf() {
        // select file and save as pdf
    }

    private fun openCameraAndSavePhotoAsPdf() {
        // open camera take photo and save as pdf
    }

    private fun saveAsPdf(text: String) {
        // save pdf
    }

    private fun viewReportPdf() {
        // view pdf in dialog
    }
}