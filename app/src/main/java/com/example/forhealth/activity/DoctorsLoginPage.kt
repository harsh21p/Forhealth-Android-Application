package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import kotlinx.android.synthetic.main.doctors_login_page.*

class DoctorsLoginPage : AppCompatActivity() {

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

        login_button.setOnClickListener(View.OnClickListener {

            // if login found directly go to landing page
            // if not found show the popup for quick login with previous login information password required
            // if nothing found show popup to insert username and password
            // all will be including guest mode button go to login and go to signup button
        })

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@DoctorsLoginPage, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        signup_button.setOnClickListener(View.OnClickListener {

            // Show popup to insert information for registration

        })
    }

}