package com.example.forhealth.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import kotlinx.android.synthetic.main.welcome_page.*
import java.lang.Boolean


class WelcomePage : AppCompatActivity() {

    private var sharedPreferences:SharedPreferences?=null
    private var prevStarted:String?="yes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide Top notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.welcome_page)

        // hide bottom navigation bar

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        back_to_splash_screen.setOnClickListener(View.OnClickListener {

            val iSplashScreen = Intent(this@WelcomePage, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        proceed_button.setOnClickListener(View.OnClickListener {
            if(cb_android.isChecked){
                checkState()
            }
            val iChoiceDoctorOrPatient = Intent(this@WelcomePage, ChoiceDoctorOrPatient::class.java)
            startActivity(iChoiceDoctorOrPatient)
            finish()
        })

    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences!!.getBoolean(prevStarted, false)) {
            startActivity(Intent(this@WelcomePage, ChoiceDoctorOrPatient::class.java))
            finish()
        }
    }

    private fun checkState(){
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(prevStarted, Boolean.TRUE)
        editor.apply()
    }

}