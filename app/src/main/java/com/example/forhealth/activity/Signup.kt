package com.example.forhealth.activity

import android.content.Intent
import android.database.Observable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forhealth.R
import com.example.forhealth.dagger.*
import com.example.forhealth.database.DataRepository
import com.example.forhealth.database.DatabaseViewModel
import com.example.forhealth.database.DatabaseViewModelFactory
import com.example.forhealth.database.MyDatabaseInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.signup.*
import kotlinx.android.synthetic.main.signup.back_button
import kotlinx.android.synthetic.main.signup.back_to_splash_screen
import kotlinx.android.synthetic.main.signup.idEdtPassword
import kotlinx.android.synthetic.main.signup.idEdtUserName
import kotlinx.android.synthetic.main.signup.main_layout
import javax.inject.Inject


class Signup : AppCompatActivity() {
    private var database= FirebaseDatabase.getInstance()
    private var myRef: DatabaseReference? = null
    @Inject
    @CommonQualifier
    lateinit var common:Services
    lateinit var mainViewModel: DatabaseViewModel

    @Inject
    lateinit var localDatabase: MyDatabaseInstance

    private var avatarSelected = 0

    lateinit var myLiveData:MyLiveData
    @Inject
    @BluetoothQualifier lateinit var bluetooth: Services

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

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(DataRepository(localDatabase.databaseDao()))).get(
            DatabaseViewModel::class.java)

        myLiveData = MyLiveData.getMyLiveData(localDatabase.databaseDao(), bluetooth)

        myLiveData.avatarSelectedMutable.observe(this, Observer {
            avatarSelected = it
            if(it == 0) {

                idEdiAvatar.setImageDrawable(
                    ContextCompat.getDrawable(this,
                        R.drawable.avatar_male
                    ));
            }else{
                if(it==1){
                    idEdiAvatar.setImageDrawable(
                        ContextCompat.getDrawable(this,
                            R.drawable.avatar_female_a
                        ));
                }else{
                    if(it==2){
                        idEdiAvatar.setImageDrawable(
                            ContextCompat.getDrawable(this,
                                R.drawable.avatar_female_b
                            ));
                    }
                }
            }
        })

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
                                                localRef!!.child("Name").setValue(idEdtUserNamePre.text.toString()+" "+idEdtUserName.text.toString()).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        localRef!!.child("Avatar").setValue(avatarSelected).addOnCompleteListener { task ->
                                                            if(task.isSuccessful) {

                                                                progress_bar_signup.visibility = View.GONE
                                                                signup_button_text.visibility = View.VISIBLE
                                                                auth.signOut()

                                                                val view = layoutInflater.inflate(R.layout.msg_dilog,null)
                                                                val msg = view.findViewById<TextView>(R.id.string_for_dilog)
                                                                msg.text = "Signup successful."

                                                                common.textDilog(view,this)

                                                                val splashScreenTimeout = 2500
                                                                Handler().postDelayed({
                                                                    startActivity(iLoginScreen)
                                                                    finish()
                                                                }, splashScreenTimeout.toLong())


                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {

                            val view = layoutInflater.inflate(R.layout.msg_dilog,null)
                            val msg = view.findViewById<TextView>(R.id.string_for_dilog)
                            msg.text = e.toString().split(":")[1]
                            common.textDilog(view,this)
                                progress_bar_signup.visibility = View.GONE
                                signup_button_text.visibility = View.VISIBLE
                                auth.signOut()
                            }
                    }
                }.addOnFailureListener { exception ->
                    progress_bar_signup.visibility=View.GONE
                    signup_button_text.visibility=View.VISIBLE
                    val view = layoutInflater.inflate(R.layout.msg_dilog,null)
                    val msg = view.findViewById<TextView>(R.id.string_for_dilog)
                    msg.text = exception.toString().split(":")[1]
                    common.textDilog(view,this)
//                    Toast.makeText(this, "Something went wrong ($exception)", Toast.LENGTH_LONG).show()
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
            common.hideKeyboard(this)
        })

        idEdiAvatarHolder.setOnClickListener(View.OnClickListener {
            val view = layoutInflater.inflate(R.layout.custom_dialog_layout_avatar,null)
            common.avatarDialogBox(view,this)
        })
    }
}