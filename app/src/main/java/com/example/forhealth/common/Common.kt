package com.example.forhealth.common
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.forhealth.R
import com.example.forhealth.bluetooth.Bluetooth
import com.example.forhealth.bluetooth.StaticReference
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.datamodel.ModelPairedDevices
import kotlinx.android.synthetic.main.doctors_landing_page.*
import kotlinx.android.synthetic.main.signup.*


class Common(context: Context){
    var mContext = context
    var bluetooth = Bluetooth(mContext)

    fun hideKeyboard() {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isAcceptingText) {
            try {
                imm.hideSoftInputFromWindow(
                    (mContext as Activity).window
                        .currentFocus!!.windowToken, 0
                )
            }catch (e:Exception){

            }

        }
    }

    fun comingSoonDialogBox(view:View) {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog).create()
        val  okButton = view.findViewById<CardView>(R.id.custom_dialog_ok_button)
        builder.setView(view)
        okButton.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    fun avatarDialogBox(imageView:ImageView,view:View) {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog).create()
        val  avatarA = view.findViewById<CardView>(R.id.custom_dialog_avatar_a)
        val  avatarB = view.findViewById<CardView>(R.id.custom_dialog_avatar_b)
        val  avatarC = view.findViewById<CardView>(R.id.custom_dialog_avatar_c)
        builder.setView(view)
        avatarA.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.avatar_male

                ));
            selectedAvatar=0
            builder.dismiss()
        }
        avatarB.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.avatar_female_a
                ));
            selectedAvatar=1
            builder.dismiss()
        }
        avatarC.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.avatar_female_b
                ));
            selectedAvatar=2
            builder.dismiss()

        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()

    }

    fun avatarDialogBoxForPatient(imageView:ImageView,view:View) {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog).create()
        val  avatarA = view.findViewById<CardView>(R.id.custom_dialog_avatar_a_p)
        val  avatarB = view.findViewById<CardView>(R.id.custom_dialog_avatar_b_p)
        val  avatarC = view.findViewById<CardView>(R.id.custom_dialog_avatar_c_p)
        val  avatarD = view.findViewById<CardView>(R.id.custom_dialog_avatar_d_p)
        builder.setView(view)
        avatarA.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.male_avatar_patient_a

                ));
            selectedPatientAvatar=0
            builder.dismiss()
        }
        avatarB.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.male_avatar_patient_b
                ));
            selectedPatientAvatar=1
            builder.dismiss()
        }
        avatarC.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.female_avatar_patient_a
                ));
            selectedPatientAvatar=2
            builder.dismiss()

        }

        avatarD.setOnClickListener {
            imageView.setImageDrawable(
                ContextCompat.getDrawable(mContext,
                    R.drawable.female_avatar_patient_b
                ));
            selectedPatientAvatar=3
            builder.dismiss()

        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()

    }


    @SuppressLint("MissingPermission")
    fun setClickForSideBar(hamburger:ImageView, controls:CardView, sidebar:CardView, all_controls_card:CardView){
        hamburger.setOnClickListener(View.OnClickListener {
            controls.visibility=View.GONE
            if(Connection){
                controls.visibility=View.VISIBLE
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
            mBtAdapter =  BluetoothAdapter.getDefaultAdapter()
            if(hamburgerVisibilityManager == 1){
                if(!mBtAdapter.isEnabled) {
                    var bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivity(mContext,bluetoothIntent,null)
                    findPairedDevices()
                }else{
                    findPairedDevices()
                }
                sidebar.visibility=View.VISIBLE
                all_controls_card.visibility=View.GONE
                hamburgerVisibilityManager = 0
            }else{
                sidebar.visibility=View.GONE
                all_controls_card.visibility=View.GONE
                hamburgerVisibilityManager = 1
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun findPairedDevices(){
        pairedDevicesList.clear()
        val pairedDevices = mBtAdapter!!.bondedDevices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                try{
                    var device = ModelPairedDevices(device.name, device.address)
                    pairedDevicesList.add(device)
                }finally {
                    pairedDevicesViewHolder.notifyDataSetChanged()
                }
            }
        }
        else{
            Toast.makeText(mContext, "Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    fun setClickForControls(view: View,sidebar: CardView,all_controls_card: CardView,controls: CardView,control_shutdown:CardView,control_reset:CardView
    ,control_set_home:CardView,control_close:CardView,control_direction_clockwise:CardView,control_direction_anticlockwise:CardView,control_brake_state:Switch,control_refresh:CardView){

        controls.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection){
                sidebar.visibility=View.GONE
                all_controls_card.visibility=View.VISIBLE
                hamburgerVisibilityManager = 1
                bluetooth.sendMessage("crall",mContext)

            }else{
                Toast.makeText(mContext,"Connect to device", Toast.LENGTH_SHORT).show()
                controls.visibility=View.GONE
                all_controls_card.visibility=View.GONE
                hamburgerVisibilityManager = 1
                sidebar.visibility=View.VISIBLE
            }
        })

        control_shutdown.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection) {
                dialogueBoxShutdown(view,all_controls_card)
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_reset.setOnClickListener(View.OnClickListener {

            if(StaticReference.Connection) {
                bluetooth.sendMessage("crall", mContext)

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }

        })

        control_set_home.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection) {
                bluetooth.sendMessage("sethome", mContext)

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_close.setOnClickListener(View.OnClickListener {
            all_controls_card.visibility=View.GONE
            hamburgerVisibilityManager = 1
            sidebar.visibility=View.GONE
//              bluetooth.sendMessage("cds1",context)
        })

        control_direction_clockwise.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> bluetooth.sendMessage("cc1", mContext)
                MotionEvent.ACTION_UP -> bluetooth.sendMessage("cs1", mContext)
            }
            v?.onTouchEvent(event) ?: true
        }

        control_direction_clockwise.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection) {
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_direction_anticlockwise.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> bluetooth.sendMessage("ca1", mContext)
                MotionEvent.ACTION_UP -> bluetooth.sendMessage("cs1", mContext)
            }
            v?.onTouchEvent(event) ?: true
        }
        control_direction_anticlockwise.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection) {

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_brake_state.setOnClickListener(View.OnClickListener {
            if(control_brake_state.isChecked){
                bluetooth.sendMessage("cebo",mContext)
                control_brake_state.isChecked=true
            }else{
                control_brake_state.isChecked=false
                bluetooth.sendMessage("cebf",mContext)
            }
        })

        control_refresh.setOnClickListener(View.OnClickListener {
            if(StaticReference.Connection) {
                bluetooth.sendMessage("crall", mContext)
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun dialogueBoxShutdown(view: View,all_controls_card:CardView,) {
        val builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.shutdown_dialog_yes_button)
        val  no = view.findViewById<CardView>(R.id.shutdown_dialog_cancel_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            bluetooth.sendMessage("shutdown",mContext)
            all_controls_card.visibility=View.GONE
            hamburgerVisibilityManager = 1
            builder.dismiss()
        }
        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }



}