package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import com.example.forhealth.dagger.ApplicationScope
import com.example.forhealth.dagger.BluetoothQualifier
import com.example.forhealth.dagger.MyLiveData
import com.example.forhealth.dagger.Services
import com.example.forhealth.database.MyDatabaseInstance
import kotlinx.android.synthetic.main.doctors_login_page.*
import javax.inject.Inject


class DoctorsLoginPage : AppCompatActivity() {

    lateinit var myLiveData : MyLiveData
    @Inject
    @BluetoothQualifier
    lateinit var bluetooth : Services

    @Inject
    lateinit var myDatabaseInstance : MyDatabaseInstance

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.doctors_login_page)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val dao = myDatabaseInstance.databaseDao()
        myLiveData = MyLiveData.getMyLiveData(dao,bluetooth)


        back_button.setOnClickListener(View.OnClickListener {

            val iChoiceDoctorOrPatient = Intent(this@DoctorsLoginPage, ChoiceDoctorOrPatient::class.java)
            startActivity(iChoiceDoctorOrPatient)
            finish()
        })

        login_button.setOnClickListener(View.OnClickListener {

                val iExistingUsers = Intent(this@DoctorsLoginPage, ExistingUsers::class.java)
                startActivity(iExistingUsers)
                finish()

        })

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@DoctorsLoginPage, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        signup_button.setOnClickListener(View.OnClickListener {

            val iSignupScreen = Intent(this@DoctorsLoginPage, Signup::class.java)
            startActivity(iSignupScreen)
            finish()

        })

        guest_button.setOnClickListener(View.OnClickListener {
            myLiveData.setCurrentCaregiver(9999)
            startActivity(Intent(this@DoctorsLoginPage, GuestMode::class.java))
            finish()
        })
    }

}