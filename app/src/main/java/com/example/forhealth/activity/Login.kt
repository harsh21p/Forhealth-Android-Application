package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forhealth.R
import com.example.forhealth.bluetooth.Bluetooth
import com.example.forhealth.common.Common
import com.example.forhealth.dagger.ApplicationScope
import com.example.forhealth.dagger.BluetoothQualifier
import com.example.forhealth.dagger.CommonQualifier
import com.example.forhealth.dagger.Services
import com.example.forhealth.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login_page.*
import kotlinx.android.synthetic.main.login_page.back_button
import kotlinx.coroutines.*
import java.lang.Thread.sleep
import javax.inject.Inject

class Login : AppCompatActivity() {
    private var database= FirebaseDatabase.getInstance()
    private var myRef: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    lateinit var mainViewModel: DatabaseViewModel
    @Inject
    lateinit var localDatabase:MyDatabaseInstance

    @Inject
    @CommonQualifier
    lateinit var common:Services

    @Inject
    @BluetoothQualifier lateinit var bluetooth: Services

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.login_page)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        try {
            database.setPersistenceEnabled(true)
            database.setPersistenceCacheSizeBytes((50 * 1000 * 1000).toLong())
        } catch (e: Exception) {

        }

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val dao = localDatabase.databaseDao()
        val repository = DataRepository(dao)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(
            DatabaseViewModel::class.java)


        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLoginPage = Intent(this@Login, DoctorsLoginPage::class.java)
            startActivity(iDoctorsLoginPage)
            finish()
        })

        auth= FirebaseAuth.getInstance()
        myRef = database!!.reference
        auth!!.signOut()

        idBtnLogin.setOnClickListener(View.OnClickListener {

            val email=idEdtUserName.text.toString()
            val password=idEdtPassword.text.toString()
            if(idEdtUserName.text.toString().isBlank()||idEdtPassword.text.toString().isBlank()){
                Toast.makeText(applicationContext,"Invalid", Toast.LENGTH_LONG).show()
            }else {
                login_button_text.visibility=View.INVISIBLE
                progressbar_login.visibility=View.VISIBLE
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        myRef!!.child("myAuthUser").child(auth!!.uid.toString()).get().addOnSuccessListener {
                            var msg = "Login Successful";

                            var name = it.child("Name").value.toString()
                            var avatar = it.child("Avatar").value.toString().toInt()
                            var email1  = it.child("Email").value.toString()
                            var pin = it.child("Pin").value.toString().toInt()

                                dao.getCaregiverByEmail(email).observe(this, Observer {
                                    try {
                                        it[0].caregiverId
                                        msg = "Already exist"
                                    }catch (e:IndexOutOfBoundsException){
                                        mainViewModel.insertCaregiver(Caregivers(0,name,avatar,email1,pin ))
                                    }
                                    auth!!.signOut()
                                    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                                    Toast.makeText(applicationContext,msg,Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(applicationContext,ExistingUsers::class.java))
                                    finish()

                                })


                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                            auth!!.signOut()
                        }
                    }
                }.addOnFailureListener { exception ->
                    login_button_text.visibility=View.VISIBLE
                    progressbar_login.visibility=View.GONE
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }})


        go_to_signup.setOnClickListener {
            var iSignupScreen = Intent(this@Login, Signup::class.java)
            startActivity(iSignupScreen)
            finish()
        }
        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@Login, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        main_layout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
        })

        forgot_password.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@Login, ForgotPassword::class.java))
            finish()

        })
    }

}
