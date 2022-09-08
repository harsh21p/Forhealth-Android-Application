package com.example.forhealth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.forhealth.bluetooth.StaticReference.*
import com.example.forhealth.database.MyDatabaseHelper
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class Bluetooth(context: Context) {
    private var context = context
    private var myDatabaseHelper:MyDatabaseHelper = MyDatabaseHelper(context)
    private var i :Int?= null
    private val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var currentDateTime:String? = null
    private var incriment = 0
    var entries: ArrayList<Entry> = ArrayList()
    private var result:String=""
    private var checkRepetitions:Boolean = false

    init {
        if(selectedPatientId!=9999) {
            i=1
        }else{
            i=0
        }
    }

    fun setupBT(address: String){

        mBtAdapter= BluetoothAdapter.getDefaultAdapter()
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
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        Log.d("status", "connected")
                        Connection=true
                    }
                    BluetoothChatService.STATE_CONNECTING -> {
                        Log.d("status", "connecting")
                    }
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                        Log.d("status", "not connected")
                        Connection=false
                    }
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readData = String(readBuf, 0, msg.arg1)
                    // message received
                    messageReceived(readData)
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    if (null != this) {
                    }
                }
                Constants.MESSAGE_TOAST -> if (null != this) {
                }
            }
        }
    }
    private fun messageReceived(readData: String) {
        var readDataList = readData.split(",")
        when (inputPageConnection) {
            1 -> {
                Log.d("EX","in 1")
                if (readDataList[0] == "refresh") {
                    try {
                        aSwitch.isChecked = readDataList[1]=="True"
                        torque.text= readDataList[3]
                        angle.text=readDataList[4]+" deg"
                        speed.text=readDataList[3]+" rpm"
                    }catch (e:Exception){

                    }
                } else {

                    if (readDataList[0] == "controldata") {
                        try {
                            torque.text = readDataList[1]
                            angle.text = readDataList[2] + " deg"
                            speed.text = readDataList[3] + " rph"
                        }catch (e:Exception){

                        }
                    }
                }
            }

            2 -> {
                Log.d("EX","in 2")
                if (readDataList[0] == "refresh") {
                    try {
                        aSwitch.isChecked = readDataList[1]=="True"
                        torque.text= readDataList[3]
                        angle.text=readDataList[4]+" deg"
                        speed.text=readDataList[3]+" rpm"
                    }catch (e:Exception){

                    }
                } else {
                    if (readDataList[0] == "controldata") {
                        try {
                            torque.text = readDataList[1]
                            angle.text = readDataList[2] + " deg"
                            speed.text = readDataList[3] + " rph"

                            if(myDatabaseHelper != null){
                                currentDateTime = sdf.format(Date())
                                try {
                                    if(checkRepetitions){
                                        Log.d("Data", "Start")
                                        result =
                                            myDatabaseHelper!!.addDataToData(mAuthString.toString(),
                                                selectedPatientId.toString(),
                                                currentExerciseId.toString(),
                                                currentDateTime,
                                                readDataList[0],
                                                i.toString())


                                    }else{
                                        Log.d("Data", "Stop")
                                    }

                                }catch (e:Exception){

                                }
                            }

                            // add graph parameter
                            setDataToLineChart(readDataList[1])
                            speedMeter(readDataList[2])
                            incriment += 1

                        }catch (e:Exception){

                        }
                    }else{
                        if(readDataList[0]=="repetition_completed") {
                            try {
                                checkRepetitions = readDataList[1] != "0"
                                currentRepetition.text = readDataList[1]
                            }catch (e:Exception){

                            }
                        }else{
                            if(readDataList[0]=="calibrated_torque") {
                                try {
                                    yAxis!!.axisMaximum = readDataList[1].toFloat() + 80
                                    yAxis!!.axisMinimum = readDataList[1].toFloat() - 80
                                }catch (e:Exception){

                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setDataToLineChart(message: String) {

        if(entries.size==20){
            entries.removeAt(0)
        }
        try {
            entries.add(Entry(incriment.toFloat(),message.toFloat()))
            val lineDataSet = LineDataSet(entries, "")
            val data = LineData(lineDataSet)
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.setDrawCircles(false)////
            lineDataSet.valueTextSize = 0f
            lineDataSet.lineWidth = 1f
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineDataSet.color = ContextCompat.getColor(context, android.R.color.black)
            lineDataSet.setDrawFilled(false)
            chart!!.data = data
            chart!!.invalidate()
        }catch (e:Exception){
            Log.d("EX",e.toString())
        }
    }

    private fun speedMeter(readData:String){
        var value = 135 + readData.toInt()
        try {
            speedMeter!!.setSpeed(value.toFloat())
        }catch (e:Exception){
            Log.d("EX",e.toString())
        }
    }

    fun sendMessage(msg:String,context: Context) {
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
                mChatService!!.write(send)
                mOutStringBuffer!!.setLength(0)
            }
        }catch (e:Exception){
            Log.e("SEND",e.toString())
        }

    }
    @SuppressLint("MissingPermission")
    fun stopChat() {
        if(mChatService !=null){
            mChatService!!.stop()
        }
        Connection=false
    }

    fun onItemClick(position: Int,context: Context,progressBar:ProgressBar,sidebar:CardView,controls:CardView) {
        val deviceAddress = pairedDevicesList[position]
        setupBT(deviceAddress.deviceId)
        Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show()
        onResumeCheck(0,context,progressBar,sidebar,controls)
    }

    private fun onResumeCheck(i:Int,context: Context,progressBar:ProgressBar,sidebar:CardView,controls:CardView) {
        val timeout = 500
        Handler().postDelayed({

            if(Connection){
                progressBar.visibility= View.GONE
                sidebar.visibility= View.GONE
                controls.visibility= View.VISIBLE
                hamburgerVisibilityManager = 1
                Toast.makeText(context,"Connected", Toast.LENGTH_SHORT).show()

            }else{
                progressBar.visibility= View.VISIBLE
                var z = i+1
                if(z<=20) {
                    onResumeCheck(z,context,progressBar,sidebar,controls)
                }else{
                    progressBar.visibility= View.GONE
                    controls.visibility= View.GONE
                    Toast.makeText(context,"Failed to connect", Toast.LENGTH_SHORT).show()
                    stopChat()
                }
            }
        }, timeout.toLong())
    }
}