package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.dagger.*
import com.example.forhealth.database.*
import com.example.forhealth.databinding.CreateNewPatientBinding
import kotlinx.android.synthetic.main.signup.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateNewPatient : AppCompatActivity() {

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var avatarSelected = 0

    @Inject
    @CommonQualifier lateinit var common : Services

    @Inject
    @BluetoothQualifier lateinit var bluetooth : Services

    var check = false

    @Inject
    lateinit var myDatabaseInstance : MyDatabaseInstance

    lateinit var mainViewModel : DatabaseViewModel

    lateinit var myLiveData : MyLiveData

    private lateinit var binding : CreateNewPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = DataBindingUtil.setContentView(this,R.layout.create_new_patient)

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

        myLiveData.avatarSelectedPatientMutable.observe(this, androidx.lifecycle.Observer {
            avatarSelected = it
            if(it == 0) {

                binding.patientProfile.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.male_avatar_patient_a
                    ));
            }else{
                if(it==1){
                    binding.patientProfile.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.male_avatar_patient_b
                        ));
                }else{
                    if(it==2){
                        binding.patientProfile.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.female_avatar_patient_a
                            ));
                    }else{
                        binding.patientProfile.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.female_avatar_patient_b
                            ));
                    }
                }
            }
        })


        binding.holderProfile.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.patient_avatar_layout,null)
            common.avatarDialogBoxForPatient(view,this)
        })

        binding.backButton.setOnClickListener(View.OnClickListener {
            val iDoctorsLandingPage = Intent(this@CreateNewPatient, DoctorsLandingPage::class.java)
            startActivity(iDoctorsLandingPage)
            finish()
        })

        binding.saveButton.setOnClickListener(View.OnClickListener {
            addData()
        })

        binding.mainLayout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            binding.sidebar.visibility = View.GONE
            binding.allControlsCard.visibility = View.GONE
        })

        bluetoothSetup()
    }

    private fun addData() {
        if(binding.patientName.text!!.isBlank() || binding.patientAge.text!!.isBlank() || binding.patientContact.text!!.isBlank()  || binding.patientWeight.text!!.isBlank() || binding.patientGender.text!!.isBlank() ){
            Toast.makeText(this,"All field's are required",Toast.LENGTH_SHORT).show()
        } else{
            val currentDateTime: String = sdf.format(Date())
            val currentDate = currentDateTime.split('_')
            mainViewModel.insertPatient(Patients(0,binding.patientName.text.toString(),myLiveData.currentCaregiverMutable.value!!,avatarSelected,currentDate[0].toLong(),binding.patientGender.text.toString(),binding.patientWeight.text.toString().toInt(),binding.patientWeight.text.toString().toInt(),binding.patientAge.text.toString().toInt(),binding.patientContact.text.toString().toLong()))
            Toast.makeText(this,"Patient added",Toast.LENGTH_SHORT).show()

            startActivity(Intent(this@CreateNewPatient, ExistingPatient::class.java))
            finish()
        }
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
}