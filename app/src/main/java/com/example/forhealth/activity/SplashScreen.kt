package com.example.forhealth.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.forhealth.R
import com.example.forhealth.dagger.ApplicationScope
import com.example.forhealth.dagger.CommonQualifier
import com.example.forhealth.dagger.Services
import kotlinx.android.synthetic.main.splash_screen.*
import javax.inject.Inject

class SplashScreen  : AppCompatActivity() {

    @Inject
    @CommonQualifier
    lateinit var common: Services

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.splash_screen)

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)


        get_start_button!!.setOnClickListener(View.OnClickListener {
            askForPermission()
        })

        val animationForLogo = AnimationUtils.loadAnimation(this, R.anim.fed_animation)
        val animationForTagline = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_animation)
        val animationForButton = AnimationUtils.loadAnimation(this,
            R.anim.bottom_to_top_animation_for_button
        )

        primary_logo!!.startAnimation(animationForLogo)
        tagline!!.startAnimation(animationForTagline)
        tagline!!.visibility= View.VISIBLE

        val splashScreenTimeout = 500
        Handler().postDelayed({
            get_start_button!!.visibility= View.VISIBLE
            get_start_button!!.startAnimation(animationForButton)

        }, splashScreenTimeout.toLong())

    }

    private fun askForPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2)
            }
        }else{
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
                }
            }else{
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                        ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                    }
                }else{

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        {
                            ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN), 2)
                        }
                    }else{
                        startActivity(Intent(this@SplashScreen, WelcomePage::class.java))
                        finish()
                    }

                }
            }
        }
    }

}