package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import com.example.forhealth.database.MyDatabaseHelper
import kotlinx.android.synthetic.main.doctors_login_page.*

class DoctorsLoginPage : AppCompatActivity() {
    private var mMyDatabaseHelper:MyDatabaseHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.doctors_login_page)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        back_button.setOnClickListener(View.OnClickListener {
            val iChoiceDoctorOrPatient = Intent(this@DoctorsLoginPage, ChoiceDoctorOrPatient::class.java)
            startActivity(iChoiceDoctorOrPatient)
            finish()
        })
        mMyDatabaseHelper= MyDatabaseHelper(this)
        login_button.setOnClickListener(View.OnClickListener {
            if(mMyDatabaseHelper!!.readData("CAREGIVERS").count>0) {
                val iExistingUsers = Intent(this@DoctorsLoginPage, ExistingUsers::class.java)
                startActivity(iExistingUsers)
                finish()
            }else{
                val iLoginScreen = Intent(this@DoctorsLoginPage, Login::class.java)
                startActivity(iLoginScreen)
                finish()
            }
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
    }

}