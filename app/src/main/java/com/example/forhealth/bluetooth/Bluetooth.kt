package com.example.forhealth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.forhealth.dagger.MyLiveData
import com.example.forhealth.dagger.Services
import com.example.forhealth.database.*
import com.google.android.material.slider.Slider
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Bluetooth @Inject constructor(context: Context,database: MyDatabaseInstance): Services {
    private var context = context
    private var mBtAdapter:BluetoothAdapter?=null
    private var mChatService:BluetoothChatService?=null
    private var mOutStringBuffer: StringBuffer?=null
    private var mInStringBuffer: StringBuffer?=null
    private var mConnectedDeviceName:String?=null
    private var myLiveData: MyLiveData
    private var readDataList:List<String>?=null

    init {
        mBtAdapter= BluetoothAdapter.getDefaultAdapter()
        myLiveData = MyLiveData.getMyLiveData(database.databaseDao(),this)

    }

    override fun setupBT(address: String){

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var portNumber = 1
        if (!sharedPreferences.getBoolean("default_port", true)) {
            val portValue = sharedPreferences.getString("port", "0")
            portNumber = portValue!!.toInt()
        }
        mChatService = BluetoothChatService(context, mHandler)
        mOutStringBuffer = StringBuffer("")
        mInStringBuffer = StringBuffer("")
        val device = mBtAdapter!!.getRemoteDevice(address)
        mChatService!!.connect(device, portNumber, true)
    }


    var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        Log.d("status", "connected")
                        myLiveData.setConnection(true)
                    }
                    BluetoothChatService.STATE_CONNECTING -> {
                        Log.d("status", "connecting")
                    }
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                        Log.d("status", "not connected")
                        if(myLiveData.btConnectionMutable.value == true) {
                            myLiveData.setConnection(false)
                        }
                    }
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    val readData = String(readBuf, 0, msg.arg1)
                    messageReceived(readData)
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    if (null != this) {
                    }
                }
                Constants.MESSAGE_TOAST -> if (null != this) {
                }
            }
        }
    }
    override fun messageReceived(readData: String) {
        readDataList = readData.split(",")
        // control_data
        if(readDataList!!.isNotEmpty()) {
            if (readDataList!![0] == "d") {
                myLiveData.setLiveData(listOf<String>(readDataList!![1],
                    readDataList!![2],
                    readDataList!![3]))
            } else {
                // refresh
                if (readDataList!![0] == "r") {
                    myLiveData.setBreakState(readDataList!![1] == "1")
                    myLiveData.setLiveData(listOf<String>(readDataList!![3],
                        readDataList!![4],
                        readDataList!![3]))
                } else {
                    // repetition_completed
                    if (readDataList!![0] == "rc") {
                        try {
                            myLiveData.setCompletedRepetition(readDataList!![1].toInt())
                        }catch (e:Exception){
                            Log.e("rc",e.toString())
                        }
                    } else {
                        // calibrated_torque
                        if (readDataList!![0] == "ct") {
                            try {
                                myLiveData.setCalibratedTorque(readDataList!![1].toFloat())
                            }catch (e:Exception){
                                Log.e("ct",e.toString())
                            }
                        } else {
                            // feedback
                            if (readDataList!![0] == "f") {
                                myLiveData.setMsg(readDataList!![1])
                            }
                        }
                    }
                }
            }
        }else{
            Log.e("Bluetooth","Data is not well formatted")
        }
    }

    @Synchronized override fun sendMessage(msg:String) {
        try {
            if (mChatService!!.state != BluetoothChatService.STATE_CONNECTED) {
                Toast.makeText(context, "cant send message - not connected", Toast.LENGTH_SHORT)
                        .show()
                return
            }
        }catch (e:Exception) {
            Log.e("PRE SEND",e.toString())
        }
        try {
            if (msg != null) {
                val send = msg.toByteArray()
                synchronized(this){
                    mChatService!!.write(send)
                    mOutStringBuffer!!.setLength(0)
                }
            }
        }catch (e:Exception){
            Log.e("SEND",e.toString())
        }

    }
    @SuppressLint("MissingPermission")
    override fun stopChat() {
        if(mChatService !=null){
            mChatService!!.stop()
        }
        myLiveData.setConnection(false)
    }



    override fun onItemClick(position: Int) {
        val deviceAddress = myLiveData.pairedDevicesList!![position]
        setupBT(deviceAddress.deviceId)
        Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show()
        val splashScreenTimeout = 5000
        Handler().postDelayed({
           if(myLiveData.btConnectionMutable.value==false){
               Toast.makeText(context,"Not Found",Toast.LENGTH_SHORT).show()
               stopChat()
           }
        }, splashScreenTimeout.toLong())
    }


    override fun hideKeyboard(mContext:Context) {
        TODO("Not yet implemented")
    }

    override fun comingSoonDialogBox(view: View,context:Context) {
        TODO("Not yet implemented")
    }

    override fun avatarDialogBox(view: View,context:Context){
        TODO("Not yet implemented")
    }

    override fun avatarDialogBoxForPatient(view: View,context: Context) {
        TODO("Not yet implemented")
    }
    override fun bluetoothInit(
        controls: CardView,
        sidebar: CardView,
        all_controls_card: CardView,
        mContext: Context
    ){
        TODO("Not yet implemented")
    }

    override fun setClickForSideBar(
        hamburger: ImageView,
        controls: CardView,
        sidebar: CardView,
        all_controls_card: CardView,mContext:Context
    ) {
        TODO("Not yet implemented")
    }

    override fun findPairedDevices() {
        TODO("Not yet implemented")
    }


    override fun setClickForControls(
        view: View,
        sidebar: CardView,
        all_controls_card: CardView,
        controls: CardView,
        control_shutdown: CardView,
        control_reset: CardView,
        control_set_home: CardView,
        control_close: CardView,
        control_direction_clockwise: CardView,
        control_direction_anticlockwise: CardView,
        control_brake_state: Switch,
        control_refresh: CardView,
        context:Context,
        speed_slider:Slider
    ) {
        TODO("Not yet implemented")
    }

    override fun dialogueBoxShutdown(view: View, all_controls_card: CardView,context: Context) {
        TODO("Not yet implemented")
    }

    override fun textDilog(view: View,context: Context) {
        TODO("Not yet implemented")
    }


}