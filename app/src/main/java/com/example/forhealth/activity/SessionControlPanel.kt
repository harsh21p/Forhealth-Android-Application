package com.example.forhealth.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.adapter.MovementViewHolder
import com.example.forhealth.dagger.*
import com.example.forhealth.database.*
import com.example.forhealth.databinding.SessionControlPanalBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class SessionControlPanel : AppCompatActivity(){

    private var movementViewHolder: MovementViewHolder?=null
    private val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var completedRepetition = 0
    @Inject
    @CommonQualifier
    lateinit var common : Services
    @Inject
    @BluetoothQualifier
    lateinit var bluetooth : Services
    var check = false
    @Inject
    lateinit var myDatabaseInstance : MyDatabaseInstance
    lateinit var mainViewModel : DatabaseViewModel
    lateinit var myLiveData : MyLiveData
    private lateinit var binding : SessionControlPanalBinding
    private var graphLastXValue = 5.0
    private var mSeries: LineGraphSeries<DataPoint>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this, R.layout.session_control_panal)


        var myComponent = (application as ApplicationScope).myComponent
        myComponent.inject(this)

        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions

        val dao = myDatabaseInstance.databaseDao()
        val repository = DataRepository(dao)

        initGraph(binding.graph!!)

//        initChart()

        myLiveData = MyLiveData.getMyLiveData(dao,bluetooth)

        myLiveData.setCompletedRepetition(9999)


        mainViewModel = ViewModelProvider(this, DatabaseViewModelFactory(repository)).get(
            DatabaseViewModel::class.java)

        mainViewModel.getSessionById(myLiveData.currentSessionMutable.value!!).observe(this,Observer{
            if(!it.isNullOrEmpty()){
                myLiveData.setCurrentSessionName(it[0].sessionName)
            }
        })

        myLiveData.currentSessionNameMutable.observe(this,Observer{
            binding.sessionName!!.text = myLiveData.currentSessionNameMutable.value
        })


        binding.mainLayout!!.setOnClickListener(View.OnClickListener {
            common.hideKeyboard(this)
            binding.sidebar!!.visibility = View.GONE
            binding.allControlsCard!!.visibility = View.GONE
        })

        myLiveData.liveDataMutable.observe(this, androidx.lifecycle.Observer {
            binding.tourqe = it[0]+" nm"
            binding.angle2 = it[1]+" deg"
            binding.speed = it[2]+" rph"

            binding.angle = it[1]

            binding.speedMeter!!.setSpeed(135 + it[1].toFloat())

//            setDataToLineChart(it[0])

            addValueToGraphView(it[0].toDouble())

            if(!myLiveData.isClickableMutable.value!!){
                val currentDateTime: String = sdf.format(Date())
                val currentDate = currentDateTime.split('_')
                mainViewModel.insertData(Data(0,myLiveData.currentCaregiverMutable.value!!,myLiveData.currentPatientMutable.value!!,myLiveData.currentSessionMutable.value!!,myLiveData.currentExerciseMutable.value!!,currentDate[0].toLong(),currentDate[1].toLong(),it[0]+","+it[1]+","+it[2]+",$completedRepetition"))
//                Log.e("Data","Start")
            }else{
//                Log.e("Data","Stop")

            }
//            bluetooth.sendMessage("send")
        })

        myLiveData.isClickableMutable.observe(this, Observer {
            binding.playpause!!.isClickable = it
        })

        myLiveData.msgMutable.observe(this, androidx.lifecycle.Observer {
            binding.msg = it
        })

        myLiveData.calibratedTorqueMutable.observe(this, Observer {

//            yAxis!!.axisMinimum = it - 80
//            yAxis!!.axisMaximum = it + 80

            binding.graph!!.viewport.setMinY((it - 101).toDouble())
            binding.graph!!.viewport.setMaxY((it + 101).toDouble())
            binding.graph!!.viewport.isYAxisBoundsManual = true;
            binding.graph!!.addSeries(mSeries)

        })

        myLiveData.breakStateMutable.observe(this, Observer {
            binding.controlBrakeState!!.isChecked = it
        })

        myLiveData.btConnectionMutable.observe(this, androidx.lifecycle.Observer {
            if(myLiveData.btConnectionMutable.value==true) {
                check = true
                binding.msg = "Connected"
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                binding.controls!!.visibility = View.VISIBLE
            }else{
                if(check) {
                    binding.msg = "Disconnected"
                    Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show()
                    check = false
                    binding.controls!!.visibility = View.GONE
                    binding.allControlsCard!!.visibility = View.GONE
                }
            }
        })

        myLiveData.completedRepetitionMutable.observe(this, androidx.lifecycle.Observer {

            if(it != 9999){
                completedRepetition = it
                binding.repetitionCompletedSessionControl!!.text = it.toString()
                if(it == 0){
                    myLiveData.setIsClickable(true)

                    if(myLiveData.currentPositionMutable.value!! < myLiveData.currentMovementListMutable.value!!.size-1){
                        dialogBoxNextMovement()
                    }else{
                        dialogBoxAllMovementCompleted()
                    }
                }
            }else{
                binding.repetitionCompletedSessionControl!!.text = "0"
            }
        })


        binding.backButton!!.setOnClickListener(View.OnClickListener {
            if(myLiveData.isClickableMutable.value!!) {
                startActivity(Intent(this, SessionInformation::class.java))
                finish()
            }else{
                alertStop( layoutInflater.inflate(R.layout.stop_alert,null))
            }
        })

        bluetoothSetup()

        myLiveData.currentMovementListMutable.value!!.removeAt(myLiveData.currentMovementListMutable.value!!.size - 1)
        movementViewHolder = MovementViewHolder(myLiveData.currentMovementListMutable.value!!,this)

        val movementRecyclerView = findViewById<RecyclerView>(R.id.movement_recycler_control_panal)
        movementRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,false)
        movementRecyclerView.adapter =  movementViewHolder

        movementViewHolder!!.notifyDataSetChanged()
        myLiveData.setCurrentExercise(myLiveData.currentMovementListMutable.value!![0].id)
        myLiveData.setCurrentPosition(0)
        myLiveData.currentPositionMutable.observe(this, androidx.lifecycle.Observer {
            myLiveData.setCurrentExercise(myLiveData.currentMovementListMutable.value!![it].id)
            setDataToUi()
        })




        binding.playpause!!.setOnClickListener(View.OnClickListener {

            if(myLiveData.isClickableMutable.value!!){
                startCurrentExercise()
            }

        })

        binding.stopsession!!.setOnClickListener(View.OnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
                bluetooth.sendMessage(",0,")
                myLiveData.setIsClickable(true)
            }else{
                common.bluetoothInit(binding.controls!!,binding.sidebar!!,binding.allControlsCard!!,this)
            }
        })

    }

    private fun alertStop(view:View) {

        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.yes_button_stop)
        val  no = view.findViewById<CardView>(R.id.no_button_stop)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            if(myLiveData.btConnectionMutable.value!!) {
                bluetooth!!.sendMessage("0")

            }else{
                Toast.makeText(this,"Device not Connected", Toast.LENGTH_SHORT).show()
            }
            myLiveData.setIsClickable(true)
            startActivity(Intent(this, SessionInformation::class.java))
            finish()
            builder.dismiss()
        }

        no.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun startCurrentExercise(){
        if(myLiveData.btConnectionMutable.value!!) {
            bluetooth.sendMessage("1,${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].exerciseType},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstAngle},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondAngle},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].thirdAngle},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].repetition},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementSpeed},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementAssistance},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementResistance},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementHoldTime},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementSpeed},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementAssistance},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementResistance},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementHoldTime},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType},${myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType},${binding.speedSlider!!.value.toInt()}")
            Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show()
            myLiveData.setIsClickable(false)
        }else{
            common.bluetoothInit(binding.controls!!,binding.sidebar!!,binding.allControlsCard!!,this)
        }
    }

    private fun setDataToUi() {

        binding.movementNoSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].movementNo

        binding.firstAngleSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstAngle
        binding.secondAngleSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondAngle
        binding.thirdAngleSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].thirdAngle

        binding.givenRepetitionSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].repetition

        binding.firstMovementTypeSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType
        binding.firstMovementSpeedSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementSpeed
        binding.firstMovementAssistanceSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementAssistance
        binding.firstMovementHoldTimeSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementHoldTime

        binding.secondMovementTypeSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType
        binding.secondMovementSpeedSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementSpeed

        binding.secondMovementAssistanceSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementAssistance
        binding.secondMovementHoldTimeSessionControl!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementHoldTime

        binding.givenExerciseType!!.text = myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].exerciseType


        if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType == "PASSIVE" || myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType == "ACTIVE"){
            binding.firstMovementAssistanceSessionControlCard!!.visibility=View.GONE
            binding.firstHoldTimeCard!!.visibility=View.GONE
            binding.firstMovementParametersAssistanceAndResistance!!.visibility=View.GONE
        }else{
            if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType == "AR ( ISOMETRIC )" || myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType == "AR ( ISOTONIC )"){
                binding.firstMovementAssistanceSessionControlCard!!.visibility=View.VISIBLE
                binding.firstMovementParametersAssistanceAndResistance!!.visibility=View.VISIBLE
                binding.firstMovementParametersAssistanceAndResistance!!.text = "Resistance"
                binding.firstHoldTimeCard!!.visibility=View.VISIBLE
                if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].firstMovementType == "AR ( ISOTONIC )"){
                    binding.firstHoldTimeCard!!.visibility=View.GONE
                }
            }else{
                binding.firstMovementAssistanceSessionControlCard!!.visibility=View.VISIBLE
                binding.firstMovementParametersAssistanceAndResistance!!.visibility=View.VISIBLE
                binding.firstMovementParametersAssistanceAndResistance!!.text = "Assistance"
                binding.firstHoldTimeCard!!.visibility=View.GONE
            }
        }

        if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType == "PASSIVE" || myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType == "ACTIVE"){
            binding.secondMovementAssistanceSessionControlCard!!.visibility=View.GONE
            binding.secondHoldTimeCard!!.visibility=View.GONE
            binding.secondMovementParameterAssistanceAndResistsnce!!.visibility=View.GONE
        }else{
            if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType == "AR ( ISOMETRIC )" || myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType == "AR ( ISOTONIC )"){
                binding.secondMovementAssistanceSessionControlCard!!.visibility=View.VISIBLE
                binding.secondMovementParameterAssistanceAndResistsnce!!.visibility=View.VISIBLE
                binding.secondMovementParameterAssistanceAndResistsnce!!.text = "Resistance"
                binding.secondHoldTimeCard!!.visibility=View.VISIBLE
                if(myLiveData.currentMovementListMutable.value!![myLiveData.currentPositionMutable.value!!].secondMovementType == "AR ( ISOTONIC )"){
                    binding.secondHoldTimeCard!!.visibility=View.GONE
                }
            }else{
                binding.secondMovementAssistanceSessionControlCard!!.visibility=View.VISIBLE
                binding.secondMovementParameterAssistanceAndResistsnce!!.visibility=View.VISIBLE
                binding.secondMovementParameterAssistanceAndResistsnce!!.text = "Assistance"
                binding.secondHoldTimeCard!!.visibility=View.GONE
            }
        }
    }

    private fun bluetoothSetup() {
        common.setClickForSideBar(binding.hamburger!!,binding.controls!!,binding.sidebar!!,binding.allControlsCard!!,this)
        val view = layoutInflater.inflate(R.layout.custom_dialog_layout_shutdown,null)
        common.setClickForControls(view,binding.sidebar!!,binding.allControlsCard!!,binding.controls!!,binding.controlShutdown!!,binding.controlReset!!,binding.controlSetHome!!,binding.controlClose!!,binding.controlDirectionClockwise!!,binding.controlDirectionAnticlockwise!!,binding.controlBrakeState!!,binding.controlRefresh!!,this,binding.speedSlider!!)

        val pairedDevicesRecyclerView = findViewById<RecyclerView>(R.id.paired_devices_recycler_view)
        pairedDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.adapter = myLiveData.pairedDevicesViewHolder

        binding.speedSlider!!.addOnChangeListener { slider, value, fromUser ->
            binding.speedValue!!.text= value.toInt().toString()+" %"
        }
    }

    fun onMovementClick(position: Int) {
        myLiveData.setCurrentPosition(position)
    }


    private fun dialogBoxAllMovementCompleted() {
        val view = layoutInflater.inflate(R.layout.all_exercises_completed,null)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.ok_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {

            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()

    }

    private fun dialogBoxNextMovement() {
        val view = layoutInflater.inflate(R.layout.want_to_start_next_movement,null)
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val  yes = view.findViewById<CardView>(R.id.yes_button)
        val  no = view.findViewById<CardView>(R.id.no_button)
        if(view.parent != null){
            (view.parent as ViewGroup).removeView(view)
        }
        builder.setView(view)
        yes.setOnClickListener {
            myLiveData.setCurrentPosition(myLiveData.currentPositionMutable.value!!+1)
            startCurrentExercise()
            builder.dismiss()

        }
        no.setOnClickListener {
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun initGraph(graph: GraphView) {
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMinX(0.0)
        graph.viewport.setMaxX(10.0)
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(10.0)
        graph.viewport.isYAxisBoundsManual = true;
        graph.viewport.setScalableY(false)
        graph.viewport.maxYAxisSize =0.5
        graph.viewport.backgroundColor = Color.rgb(251,252,251)
        graph.gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.NONE;
        graph.gridLabelRenderer.textSize = 15f;
        graph.gridLabelRenderer.isHorizontalLabelsVisible =false
        mSeries = LineGraphSeries()
        mSeries!!.isDrawDataPoints = false
        mSeries!!.isDrawBackground = false
        mSeries!!.setAnimated(false)
        mSeries!!.thickness = 3
        graph.addSeries(mSeries)

    }

    private fun addValueToGraphView(value: Double){
        graphLastXValue += 0.25
        mSeries!!.appendData(DataPoint(graphLastXValue, value), true, 40)
    }

//    var xAxis: XAxis? = null
//    var yAxis: YAxis? = null
//    private var increment = 0
//    var entries: ArrayList<Entry> = ArrayList()
//    var chart: LineChart? = null

//    private fun initChart() {
//                chart = binding.lineChart
//                chart!!.axisLeft.setDrawGridLines(false)
//                xAxis = chart!!.xAxis
//                yAxis = chart!!.axisLeft
//                xAxis!!.setDrawGridLines(false)
//                xAxis!!.setDrawAxisLine(false)
//                chart!!.axisRight.isEnabled = false
//                chart!!.legend.isEnabled = false
//                chart!!.description.isEnabled = false
//                chart!!.animateX(1000, Easing.EaseInSine)
//                xAxis!!.position = XAxis.XAxisPosition.BOTTOM
//                xAxis!!.setDrawLabels(false)
//                yAxis!!.setDrawLabels(true)
//                xAxis!!.granularity = 1f
//    }

//    private fun setDataToLineChart(message: String) {
//
//        if(entries.size==20){
//            entries.removeAt(0)
//        }
//        try {
//            entries.add(Entry(increment.toFloat(),message.toFloat()))
//            val lineDataSet = LineDataSet(entries, "")
//            val data = LineData(lineDataSet)
//            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//            lineDataSet.setDrawCircles(false)
//            lineDataSet.valueTextSize = 0f
//            lineDataSet.lineWidth = 1f
//            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
//            lineDataSet.color = ContextCompat.getColor(this, android.R.color.black)
//            lineDataSet.setDrawFilled(false)
//            chart!!.data = data
//            chart!!.invalidate()
//        }catch (e: Exception){
//            Log.d("EX",e.toString())
//        }
//    }


//    private fun graphTest(){
//        var i = 0
//        var im = true
//
//        binding.resetsession!!.setOnClickListener(View.OnClickListener {

//            if(im){
//                addValueToGraphView(i.toDouble())
//                i+=Random.nextInt(0,5)
//                if(i>=70){
//                    im = false
//                }
//            }else{
//                addValueToGraphView(i.toDouble())
//                i-=Random.nextInt(0,5)
//                if(i<=-70){
//                    im = true
//                }
//            }

//            bluetooth.sendMessage("send")

//        })
//    }


}