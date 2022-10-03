package com.example.forhealth.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.ExistingUsersViewHolder
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.database.MyDatabaseHelper
import com.example.forhealth.datamodel.ModelExistingUsers
import kotlinx.android.synthetic.main.existing_users.*

class ExistingUsers : AppCompatActivity() {

    private var existingUsersList = ArrayList<ModelExistingUsers>()
    private val existingUsersAdapter =  ExistingUsersViewHolder(existingUsersList,this)
    private var mMyDatabaseHelper:MyDatabaseHelper?=null
    private var position = 9999
    private var identifier = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.existing_users)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        mAuthString = 9999
        selectedPatientId = 9999
        currentSessionId = 9999
        selectedSessionName = null

        back_to_splash_screen.setOnClickListener(View.OnClickListener {
            val iSplashScreen = Intent(this@ExistingUsers, SplashScreen::class.java)
            startActivity(iSplashScreen)
            finish()
        })
        mMyDatabaseHelper= MyDatabaseHelper(this)

        back_button.setOnClickListener(View.OnClickListener {
            val iDoctorsLogin = Intent(this@ExistingUsers, DoctorsLoginPage::class.java)
            startActivity(iDoctorsLogin)
            finish()
        })

        val existingUsersRecyclerView = findViewById<RecyclerView>(R.id.existing_users_recyclerview)
        existingUsersRecyclerView.layoutManager = LinearLayoutManager(this@ExistingUsers,
            LinearLayoutManager.HORIZONTAL,false)
        existingUsersRecyclerView.adapter = existingUsersAdapter


        var cursor = mMyDatabaseHelper!!.readData("CAREGIVERS")
//        Toast.makeText(this, dbpath,Toast.LENGTH_SHORT).show()

        while (cursor.moveToNext()){

            try {
                var model = ModelExistingUsers(
                    1,
                    cursor.getString(1),
                    cursor.getString(4).toInt(),
                    cursor.getString(0).toInt()
                )
                existingUsersList.add(model)
            }catch (e:Exception){
                mMyDatabaseHelper!!.deleteIntEntry(cursor.getString(0).toString(),"CAREGIVER_ID","CAREGIVERS")
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
            }
        }
        existingUsersList.add(ModelExistingUsers(2,"Someone",2,0))
        existingUsersAdapter.notifyDataSetChanged()

    }

    fun onItemClick(position: Int) {

        if(position == existingUsersList.size-1){
            val iLogin = Intent(this@ExistingUsers, Login::class.java)
            startActivity(iLogin)
            finish()
        }else{
            val view = layoutInflater.inflate(R.layout.custom_dialog_login_pin,null)
            mAuthString = existingUsersList[position].doctorId
            loginPinDialogBox(this,view,existingUsersList[position].nameOfDoctor,existingUsersList[position].avatarOfDoctor)

        }

    }

    private fun loginPinDialogBox(mContext: Context, view:View,name:String,avatar:Int) {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog).create()
        val  avatarImage: ImageView = view.findViewById<ImageView>(R.id.custom_dialog_existing_user_avatar)
        val  avatarName: TextView = view.findViewById<TextView>(R.id.custom_dialog_existing_user_name)
        avatarName.text=name
        if(avatar==0){
            avatarImage.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.avatar_male
                ));
        }else{
            if(avatar==1){
                avatarImage.setImageDrawable(
                    ContextCompat.getDrawable(mContext,
                        R.drawable.avatar_female_a
                    ));
            }
            else{
                avatarImage.setImageDrawable(
                    ContextCompat.getDrawable(mContext,
                        R.drawable.avatar_female_b
                    ));
            }
        }
        val  idEdtPin1: EditText = view.findViewById<EditText>(R.id.custom_dialog_login_pin_1)
        val  idEdtPin2: EditText = view.findViewById<EditText>(R.id.custom_dialog_login_pin_2)
        val  idEdtPin3: EditText = view.findViewById<EditText>(R.id.custom_dialog_login_pin_3)
        val  idEdtPin4: EditText = view.findViewById<EditText>(R.id.custom_dialog_login_pin_4)
        val  progressBar: ProgressBar = view.findViewById<ProgressBar>(R.id.custom_dialog_progress_bar)
        builder.setView(view)

        idEdtPin1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (idEdtPin1.text.toString().length==1)
                { idEdtPin2.requestFocus() } }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
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
                count: Int, after: Int,
            ) {


            }
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
                count: Int, after: Int,
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })

        idEdtPin4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (idEdtPin4.text.toString().length==1)
                { if(idEdtPin1.text.toString().isBlank() || idEdtPin2.text.toString().isBlank() || idEdtPin3.text.toString().isBlank() || idEdtPin4.text.toString().isBlank() ){

                }else {
                    progressBar.visibility=View.VISIBLE
                    val timeout = 1000
                    Handler().postDelayed({

                        var pin = idEdtPin1.text.toString() + idEdtPin2.text.toString() + idEdtPin3.text.toString() + idEdtPin4.text.toString()
                        var cursor = mMyDatabaseHelper!!.readDataByIntID(mAuthString, "CAREGIVER_ID","CAREGIVERS")
                        cursor.moveToFirst()
                        if (pin == cursor.getString(2)) {

                            builder.dismiss()
                            progressBar.visibility = View.INVISIBLE
                            if(identifier != 1) {
                                val iDoctorsLandingPage =
                                    Intent(this@ExistingUsers, DoctorsLandingPage::class.java)
                                startActivity(iDoctorsLandingPage)
                                finish()

                            }else{
                                mMyDatabaseHelper!!.deleteIntEntry(mAuthString.toString(),
                                    "CAREGIVER_ID",
                                    "CAREGIVERS")
                                mMyDatabaseHelper!!.deleteIntEntry(mAuthString.toString(),
                                    "CAREGIVER_ID_IN_PATIENT",
                                    "PATIENTS")
                                mMyDatabaseHelper!!.deleteIntEntry(mAuthString.toString(),
                                    "CAREGIVER_ID_IN_EXERCISE",
                                    "EXERCISES")
                                mMyDatabaseHelper!!.deleteIntEntry(mAuthString.toString(),
                                    "CAREGIVER_ID_IN_SESSIONS",
                                    "SESSIONS")
                                mMyDatabaseHelper!!.deleteIntEntry(mAuthString.toString(),
                                    "CAREGIVER_ID_IN_DATA",
                                    "DATA")
                                existingUsersList.removeAt(position)
                                existingUsersAdapter.notifyDataSetChanged()
                                identifier = 0

                                if(existingUsersList.isEmpty()){
                                    var iLogin = Intent(this@ExistingUsers, Login::class.java)
                                    startActivity(iLogin)
                                    finish()
                                }
                            }

                        } else {
                            progressBar.visibility=View.INVISIBLE
                            Toast.makeText(mContext, "Wrong pin try again ", Toast.LENGTH_SHORT)
                                .show()
                            builder.dismiss()
                            identifier = 0
                        }

                    }, timeout.toLong())
                }

                }else{
                    idEdtPin3.requestFocus()
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {}
            override fun afterTextChanged(s: Editable) {

            }
        })

        builder.setCanceledOnTouchOutside(true)
        builder.setOnDismissListener(DialogInterface.OnDismissListener {
            identifier = 0
        })
        builder.show()

        idEdtPin1.requestFocus()
    }

    fun onDeleteClick(position: Int) {

        mAuthString = existingUsersList[position].doctorId
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_delete,null)
        dialogueBoxDelete(view,position)

    }

    fun dialogueBoxDelete(view: View,position: Int) {
        this.position = position
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.delete_dialog_yes_button)
        val  no = view.findViewById<CardView>(R.id.delete_dialog_cancel_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            identifier = 1
            val view = layoutInflater.inflate(R.layout.custom_dialog_login_pin,null)
            loginPinDialogBox(this,view,existingUsersList[position].nameOfDoctor,existingUsersList[position].avatarOfDoctor)
            builder.dismiss()
        }
        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }
}