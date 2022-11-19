package com.example.forhealth.activity

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.forhealth.R
import com.example.forhealth.common.Common
import com.example.forhealth.dagger.ApplicationScope
import com.example.forhealth.dagger.CommonQualifier
import com.example.forhealth.dagger.Services
import com.example.forhealth.database.DataRepository
import com.example.forhealth.database.DatabaseViewModel
import com.example.forhealth.database.DatabaseViewModelFactory
import com.example.forhealth.database.MyDatabaseInstance
import kotlinx.android.synthetic.main.choice_doctor_or_patient.*
import javax.inject.Inject

class ChoiceDoctorOrPatient : AppCompatActivity() {

    @Inject
    @CommonQualifier
    lateinit var common: Services

    lateinit var mainViewModel: DatabaseViewModel
    @Inject
    lateinit var localDatabase: MyDatabaseInstance

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


        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val dao = localDatabase.databaseDao()
        val repository = DataRepository(dao)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(
            DatabaseViewModel::class.java)


        // gray card
        patient_image.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f)})
        physiotherapist_login_card.setOnClickListener(View.OnClickListener {
            val iPhysiotherapistLoginPage = Intent(this@ChoiceDoctorOrPatient, DoctorsLoginPage::class.java)
            startActivity(iPhysiotherapistLoginPage)
        })

        patient_login_card.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.custom_dialog_layout,null)
            common.comingSoonDialogBox(view,this)
        })

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@ChoiceDoctorOrPatient, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })
    }

}