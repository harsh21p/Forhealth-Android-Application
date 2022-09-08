package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import com.example.forhealth.bluetooth.StaticReference.selectedAvatar
import com.example.forhealth.common.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.signup.*
import kotlinx.android.synthetic.main.signup.back_button
import kotlinx.android.synthetic.main.signup.back_to_splash_screen
import kotlinx.android.synthetic.main.signup.idEdtPassword
import kotlinx.android.synthetic.main.signup.idEdtUserName
import kotlinx.android.synthetic.main.signup.main_layout


class Signup : AppCompatActivity() {
    private var database= FirebaseDatabase.getInstance()
    private var myRef: DatabaseReference? = null
    val common = Common(this)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.signup)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        try {
            database.setPersistenceEnabled(true)
            database.setPersistenceCacheSizeBytes((50 * 1000 * 1000).toLong())
        } catch (e: Exception) {

        }
        var auth= FirebaseAuth.getInstance()
        myRef = database!!.reference


        idEdtPin1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (idEdtPin1.text.toString().length==1)
                { idEdtPin2.requestFocus() } }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })

        idEdtPin2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (idEdtPin2.text.toString().length==1)
                { idEdtPin3.requestFocus() }else{
                    idEdtPin1.requestFocus()
                } }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })

        idEdtPin3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (idEdtPin3.text.toString().length==1)
                { idEdtPin4.requestFocus() }else{
                    idEdtPin2.requestFocus()
                } }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })

        idEdtPin4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (idEdtPin4.text.toString().length==1)
                {  }else{
                    idEdtPin3.requestFocus()
                } }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}
            override fun afterTextChanged(s: Editable) {

            }
        })

        val iLoginScreen = Intent(this@Signup, Login::class.java)

        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLoginPage = Intent(this@Signup, DoctorsLoginPage::class.java)
            startActivity(iDoctorsLoginPage)
            finish()
        })

        idBtnSignup.setOnClickListener(View.OnClickListener {

            val email = idEdtEmail.text.toString()
            val password = idEdtPassword.text.toString()
            val pin = idEdtPin1.text.toString()+idEdtPin2.text.toString()+idEdtPin3.text.toString()+idEdtPin4.text.toString()
            if (idEdtPassword.text.toString().isBlank() || idEdtEmail.text.toString().isBlank() || pin.length<4||idEdtUserName.text.toString().isBlank()) {
                Toast.makeText(applicationContext, "All fields are required", Toast.LENGTH_LONG).show()
            } else {
                progress_bar_signup.visibility=View.VISIBLE
                signup_button_text.visibility=View.INVISIBLE
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var localRef = myRef!!.child("myAuthUser").child(auth!!.uid.toString())
                        try {
                                localRef!!.child("Pin").setValue(pin).addOnCompleteListener { task ->
                                    if(task.isSuccessful) {
                                        localRef!!.child("Email").setValue(email).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                localRef!!.child("Name").setValue(idEdtUserName.text.toString()).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        localRef!!.child("Avatar").setValue(selectedAvatar).addOnCompleteListener { task ->
                                                            if(task.isSuccessful) {
                                                                progress_bar_signup.visibility = View.GONE
                                                                signup_button_text.visibility = View.VISIBLE
                                                                auth.signOut()
                                                                startActivity(iLoginScreen)
                                                                finish()
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this, "Something went wrong please contact support ", Toast.LENGTH_LONG).show()
                                progress_bar_signup.visibility = View.GONE
                                signup_button_text.visibility = View.VISIBLE
                                auth.signOut()
                            }
                    }
                }.addOnFailureListener { exception ->
                    progress_bar_signup.visibility=View.GONE
                    signup_button_text.visibility=View.VISIBLE
                    Toast.makeText(this, "Something went wrong ($exception)", Toast.LENGTH_LONG).show()
                }
            }
        })

        go_to_login_page.setOnClickListener {
            startActivity(iLoginScreen)
            finish()
        }
        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@Signup, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        main_layout.setOnClickListener(View.OnClickListener {
            val common = Common(this)
            common.hideKeyboard()
        })

        idEdiAvatarHolder.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.custom_dialog_layout_avatar,null)
            common.avatarDialogBox(idEdiAvatar,view)
        })
    }
}