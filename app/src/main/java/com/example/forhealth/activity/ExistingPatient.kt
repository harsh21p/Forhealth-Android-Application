package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.ExistingPatientViewHolder
import com.example.forhealth.dagger.*
import com.example.forhealth.database.DataRepository
import com.example.forhealth.database.DatabaseViewModel
import com.example.forhealth.database.DatabaseViewModelFactory
import com.example.forhealth.database.MyDatabaseInstance
import com.example.forhealth.databinding.ExistingPatientBinding
import com.example.forhealth.datamodel.ModelExistingPatient
import java.util.ArrayList
import javax.inject.Inject

class ExistingPatient: AppCompatActivity() {

    private var existingPatientList = ArrayList<ModelExistingPatient>()
    private val existingPatientAdapter =  ExistingPatientViewHolder(existingPatientList,this)
    lateinit var myLiveData: MyLiveData

    private lateinit var binding : ExistingPatientBinding

    lateinit var mainViewModel: DatabaseViewModel

    @Inject
    @CommonQualifier lateinit var common : Services

    @Inject
    @BluetoothQualifier lateinit var bluetooth: Services

    @Inject
    lateinit var myDatabaseInstance: MyDatabaseInstance

    private var check = false


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide notification bsr

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this,R.layout.existing_patient)

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
            val iDoctorsLandingPage = Intent(this@ExistingPatient, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })


        binding.mainLayout!!.setOnClickListener(View.OnClickListener {
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
        })

        bluetoothSetup()
        patientRecycler()
    }

    private fun patientRecycler(){
        val existingPatientRecyclerView = findViewById<RecyclerView>(R.id.recycler_patient)
        existingPatientRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)
        existingPatientRecyclerView.adapter = existingPatientAdapter

        mainViewModel.getPatientByCaregiver(myLiveData.currentCaregiverMutable.value!!).observe(this,
            Observer {
                if (!it.isNullOrEmpty()){
                    existingPatientList.clear()
                    for (i in it!!) {
                        existingPatientList.add(ModelExistingPatient(i.patientAvatar,i.patientName,i.patientAge,i.patientContact.toString(),i.patientDate.toString(),i.patientId))
                    }
                    existingPatientAdapter.notifyDataSetChanged()
                }
            })
        existingPatientAdapter.notifyDataSetChanged()

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

    fun onClickPatient(position:Int) {
        myLiveData.setCurrentPatient(existingPatientList[position].id)
        startActivity(Intent(this@ExistingPatient, PatientProfilePage::class.java))
        finish()
    }
}