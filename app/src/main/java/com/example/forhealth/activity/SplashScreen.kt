package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import kotlinx.android.synthetic.main.splash_screen.*

class SplashScreen  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide Top notification bar

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // layout

        setContentView(R.layout.splash_screen)

        // hide bottom navigation bar

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        // code

        val iWelcomePage = Intent(this@SplashScreen, WelcomePage::class.java)
        get_start_button.setOnClickListener(View.OnClickListener {
            startActivity(iWelcomePage)
            finish()
        })

        val animationForLogo = AnimationUtils.loadAnimation(this, R.anim.fed_animation)
        val animationForTagline = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_animation)
        val animationForButton = AnimationUtils.loadAnimation(this,
            R.anim.bottom_to_top_animation_for_button
        )

        primary_logo.startAnimation(animationForLogo)
        tagline.startAnimation(animationForTagline)
        tagline.visibility= View.VISIBLE

        // start new animation after given 2 sec

        val splashScreenTimeout = 500
        Handler().postDelayed({

            get_start_button.visibility= View.VISIBLE
            get_start_button.startAnimation(animationForButton)

        }, splashScreenTimeout.toLong())

    }
}