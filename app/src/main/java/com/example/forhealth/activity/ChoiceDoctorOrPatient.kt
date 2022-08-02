package com.example.forhealth.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.forhealth.R
import kotlinx.android.synthetic.main.choice_doctor_or_patient.*

class ChoiceDoctorOrPatient : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide notification bsr

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.choice_doctor_or_patient)

        // hide bottom navigation bar

        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        val decorView = window.decorView
        decorView.systemUiVisibility = uiOptions

        // gray card

        patient_image.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f)})
        physiotherapist_login_card.setOnClickListener(View.OnClickListener {
            val iPhysiotherapistLoginPage = Intent(this@ChoiceDoctorOrPatient, DoctorsLoginPage::class.java)
            startActivity(iPhysiotherapistLoginPage)
        })

        patient_login_card.setOnClickListener(View.OnClickListener {
            comingSoonDialogBox()
        })

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@ChoiceDoctorOrPatient, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })
    }

    private fun comingSoonDialogBox() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)
        val  okButton = view.findViewById<CardView>(R.id.custom_dialog_ok_button)
        builder.setView(view)
        okButton.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }
}