package com.example.forhealth.common
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.forhealth.R
import com.example.forhealth.dagger.BluetoothQualifier
import com.example.forhealth.dagger.MyLiveData
import com.example.forhealth.dagger.Services
import com.example.forhealth.database.DataRepository
import com.example.forhealth.database.DatabaseViewModel
import com.example.forhealth.database.MyDatabaseInstance
import com.example.forhealth.datamodel.ModelPairedDevices
import com.google.android.material.slider.Slider
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Common @Inject constructor(context: Context, @BluetoothQualifier bluetooth: Services, database:MyDatabaseInstance) : Services{
    var mContext = context
    var bluetooth = bluetooth
    var selectedAvatar = 0
    var selectedPatientAvatar = 0
    var mBtAdapter:BluetoothAdapter?=null
    var mainViewModel: DatabaseViewModel
    private var myLiveData: MyLiveData

    init {
        mainViewModel = DatabaseViewModel(DataRepository(database.databaseDao()))
        myLiveData = MyLiveData.getMyLiveData(database.databaseDao(),bluetooth)
    }

    override fun hideKeyboard(mContext:Context) {

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

    override fun comingSoonDialogBox(view:View,context:Context) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
        val  okButton = view.findViewById<CardView>(R.id.custom_dialog_ok_button)
        builder.setView(view)
        okButton.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    override fun avatarDialogBox(view:View,context:Context){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
        val  avatarA = view.findViewById<CardView>(R.id.custom_dialog_avatar_a)
        val  avatarB = view.findViewById<CardView>(R.id.custom_dialog_avatar_b)
        val  avatarC = view.findViewById<CardView>(R.id.custom_dialog_avatar_c)
        builder.setView(view)
        avatarA.setOnClickListener {
            selectedAvatar = 0
            builder.dismiss()
        }
        avatarB.setOnClickListener {
            selectedAvatar = 1
            builder.dismiss()
        }
        avatarC.setOnClickListener {
            selectedAvatar = 2

            builder.dismiss()
        }
        builder.setOnDismissListener(DialogInterface.OnDismissListener {
            myLiveData.setAvatarSelected(selectedAvatar)
        })
        builder.setCanceledOnTouchOutside(true)
        builder.show()

    }

    override fun avatarDialogBoxForPatient(view:View,context: Context) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
        val  avatarA = view.findViewById<CardView>(R.id.custom_dialog_avatar_a_p)
        val  avatarB = view.findViewById<CardView>(R.id.custom_dialog_avatar_b_p)
        val  avatarC = view.findViewById<CardView>(R.id.custom_dialog_avatar_c_p)
        val  avatarD = view.findViewById<CardView>(R.id.custom_dialog_avatar_d_p)
        builder.setView(view)
        avatarA.setOnClickListener {
            selectedPatientAvatar=0
            builder.dismiss()
        }
        avatarB.setOnClickListener {
            selectedPatientAvatar=1
            builder.dismiss()
        }
        avatarC.setOnClickListener {
            selectedPatientAvatar=2
            builder.dismiss()
        }

        avatarD.setOnClickListener {
            selectedPatientAvatar=3
            builder.dismiss()

        }
        builder.setOnDismissListener(DialogInterface.OnDismissListener {
            myLiveData.setAvatarSelectedPatient(selectedPatientAvatar)
        })
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    @SuppressLint("MissingPermission")
    override fun setClickForSideBar(hamburger:ImageView, controls:CardView, sidebar:CardView, all_controls_card:CardView,mContext:Context){
        hamburger.setOnClickListener(View.OnClickListener {
            bluetoothInit(controls,sidebar,all_controls_card,mContext)
        })
    }


    override fun bluetoothInit(controls:CardView,sidebar:CardView,all_controls_card: CardView,mContext: Context){
        controls.visibility = View.GONE
        if(myLiveData.btConnectionMutable.value!!){
            controls.visibility=View.VISIBLE
        }else{
            Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
        }

        mBtAdapter =  BluetoothAdapter.getDefaultAdapter()

        if(sidebar.visibility == View.GONE){

            if(!mBtAdapter!!.isEnabled) {
                var bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(mContext,bluetoothIntent,null)
                findPairedDevices()
            }else{
                findPairedDevices()
            }
            sidebar.visibility = View.VISIBLE
            all_controls_card.visibility=View.GONE
        }else{
            sidebar.visibility=View.GONE
            all_controls_card.visibility=View.GONE
        }
    }


    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    override fun findPairedDevices(){

        myLiveData.pairedDevicesList.clear()

        val pairedDevices = mBtAdapter!!.bondedDevices

        if (pairedDevices.size > 0) {

            for (device in pairedDevices) {

                try{

                    var device = ModelPairedDevices(device.name, device.address)
                    myLiveData.pairedDevicesList!!.add(device)

                }finally {

                    myLiveData.pairedDevicesViewHolder.notifyDataSetChanged()

                }
            }
        }
        else{
            Toast.makeText(mContext, "Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setClickForControls(view: View, sidebar: CardView, all_controls_card: CardView, controls: CardView, control_shutdown:CardView, control_reset:CardView
                            , control_set_home:CardView, control_close:CardView, control_direction_clockwise:CardView, control_direction_anticlockwise:CardView, control_brake_state:Switch, control_refresh:CardView,context: Context,speed_slider:Slider){

        controls.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!){
                sidebar.visibility=View.GONE
                all_controls_card.visibility=View.VISIBLE
                bluetooth.sendMessage("rf")
            }
        })

        control_shutdown.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
                dialogueBoxShutdown(view,all_controls_card,context)
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_reset.setOnClickListener(View.OnClickListener {

            if(myLiveData.btConnectionMutable.value!!) {
                bluetooth.sendMessage("rt")

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }

        })

        control_set_home.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
                bluetooth.sendMessage("sh")

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_close.setOnClickListener(View.OnClickListener {
            all_controls_card.visibility=View.GONE
            sidebar.visibility=View.GONE
              bluetooth.sendMessage("cc")
        })

        control_direction_clockwise.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> bluetooth.sendMessage("fd,${speed_slider.value.toInt()},zz")
                MotionEvent.ACTION_UP -> bluetooth.sendMessage(",s,")
            }
            v?.onTouchEvent(event) ?: true
        }

        control_direction_clockwise.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_direction_anticlockwise.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> bluetooth.sendMessage("bd,${speed_slider.value.toInt()},zz")
                MotionEvent.ACTION_UP -> bluetooth.sendMessage(",s,")
            }
            v?.onTouchEvent(event) ?: true
        }
        control_direction_anticlockwise.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {

            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })

        control_brake_state.setOnClickListener(View.OnClickListener {
            if(control_brake_state.isChecked){
                bluetooth.sendMessage("bo")
                control_brake_state.isChecked=true
            }else{
                control_brake_state.isChecked=false
                bluetooth.sendMessage("bf")
            }
        })

        control_refresh.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
                bluetooth.sendMessage("rf")
            }else{
                Toast.makeText(mContext,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun dialogueBoxShutdown(view: View,all_controls_card:CardView,context: Context) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.shutdown_dialog_yes_button)
        val  no = view.findViewById<CardView>(R.id.shutdown_dialog_cancel_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            bluetooth.sendMessage("sd")
            builder.dismiss()
        }
        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    override fun textDilog(view:View,context: Context) {

        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).create()
        builder.setView(view)
        builder.setCanceledOnTouchOutside(true)
        builder.show()

        val splashScreenTimeout = 2500
        Handler().postDelayed({
            builder.dismiss()
        }, splashScreenTimeout.toLong())


    }

    override fun setupBT(address: String) {
        TODO("Not yet implemented")
    }

    override fun messageReceived(readData: String) {
        TODO("Not yet implemented")
    }


    override fun sendMessage(msg: String) {
        TODO("Not yet implemented")
    }

    override fun stopChat() {
        TODO("Not yet implemented")
    }

    override fun onItemClick(
        position: Int
    ) {
        TODO("Not yet implemented")
    }


}