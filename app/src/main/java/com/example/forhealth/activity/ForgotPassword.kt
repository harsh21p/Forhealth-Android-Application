package com.example.forhealth.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.forhealth.R
import com.example.forhealth.common.Common
import com.example.forhealth.dagger.ApplicationScope
import com.example.forhealth.dagger.CommonQualifier
import com.example.forhealth.dagger.Services
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.forgot_password.*
import javax.inject.Inject

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    @Inject
    @CommonQualifier
    lateinit var common:Services

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.forgot_password)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        back_button.setOnClickListener(View.OnClickListener {
            val iLoginPage = Intent(this@ForgotPassword, Login::class.java)
            startActivity(iLoginPage)
            finish()
        })

        auth= FirebaseAuth.getInstance()
        auth!!.signOut()

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@ForgotPassword, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })

        main_layout.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
        })

        send_text.visibility = View.GONE

        send_password_reset_link.setOnClickListener(View.OnClickListener {

            if(email_id.text.toString().isNotBlank()){
                progressbar_reset.visibility = View.VISIBLE
                reset_button_text.visibility = View.INVISIBLE
               auth.setLanguageCode("en")
               auth.sendPasswordResetEmail(email_id.text.toString()).addOnFailureListener(this) { exception: Exception ->
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                   progressbar_reset.visibility = View.GONE
                   reset_button_text.visibility = View.VISIBLE
               }.addOnSuccessListener {
                   Toast.makeText(this,"Sent",Toast.LENGTH_SHORT).show()
                   send_text.visibility = View.VISIBLE
                   progressbar_reset.visibility = View.GONE
                   reset_button_text.text = "Resend"
                   reset_button_text.visibility = View.VISIBLE
               }
            }else{
                Toast.makeText(this,"Invalid input",Toast.LENGTH_SHORT).show()
            }

        })

    }

}
