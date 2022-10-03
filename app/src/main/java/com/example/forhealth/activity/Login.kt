package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.common.Common
import com.example.forhealth.database.MyDatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login_page.*
import kotlinx.android.synthetic.main.login_page.back_button
import java.lang.Thread.sleep

class Login : AppCompatActivity() {
    private var database= FirebaseDatabase.getInstance()
    private var myRef: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    private var mMyDatabaseHelper:MyDatabaseHelper?=null

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

        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLoginPage = Intent(this@Login, DoctorsLoginPage::class.java)
            startActivity(iDoctorsLoginPage)
            finish()
        })

        mMyDatabaseHelper=MyDatabaseHelper(this)
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
                            var msg:String?=null;
                            var iterations = 0
                            while (true) {
                                sleep(1000)
                                try {
                                    msg = mMyDatabaseHelper!!.addDataToCaregivers(
                                        it.child("Name").value.toString(),
                                        it.child("Email").value.toString(),
                                        it.child("Pin").value.toString(),
                                        it.child("Avatar").value.toString()
                                    )
                                    auth!!.signOut()
                                    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
                                    var iExistingUsers = Intent(this@Login, ExistingUsers::class.java)
                                    startActivity(iExistingUsers)
                                    finish()
                                    break
                                }catch (e:Exception){
                                    iterations += 1
                                    if(iterations == 10){
                                        Toast.makeText(this, "Something went wrong ($e)", Toast.LENGTH_SHORT).show()
                                        Log.e("EX",e.toString())
                                        break
                                    }else {
                                        continue
                                    }
                                }
                            }

                            login_button_text.visibility=View.VISIBLE
                            progressbar_login.visibility=View.GONE

                            auth!!.signOut()

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
            val common = Common(this)
            common.hideKeyboard()
        })

        forgot_password.setOnClickListener(View.OnClickListener {

            val iForgotPassword = Intent(this@Login, ForgotPassword::class.java)
            startActivity(iForgotPassword)
            finish()

        })
    }

}
