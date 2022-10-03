package com.example.forhealth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.forhealth.adapter.PairedDevicesViewHolder;
import com.example.forhealth.datamodel.ModelExercise;
import com.example.forhealth.datamodel.ModelPairedDevices;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;

import in.unicodelabs.kdgaugeview.KdGaugeView;

public class StaticReference {

    public static Boolean Connection = false;
    public static int inputPageConnection = 0;

    public static BluetoothAdapter mBtAdapter = null;
    public static BluetoothChatService mChatService = null;
    public static StringBuffer mOutStringBuffer = null;
    public static String mConnectedDeviceName = null;
    public static StringBuffer mInStringBuffer = null;

    public static int mAuthString = 9999;

    public static int selectedPatientId = 9999;
    public static int currentSessionId = 9999;
    public static String selectedSessionName = "GS01";
    public static int currentExerciseId = 9999;

    public static int selectedAvatar = 0;
    public static int selectedPatientAvatar = 0;

    public static int hamburgerVisibilityManager;
    public static ArrayList<ModelPairedDevices> pairedDevicesList = new ArrayList<ModelPairedDevices>();
    public static PairedDevicesViewHolder pairedDevicesViewHolder ;

    public static Switch aSwitch;
    public static TextView torque;
    public static TextView angle;
    public static TextView speed;
    public static TextView currentRepetition;

    public static TextView feedBack;
    public static TextView angleValue;
    public static TextView speedValue;
    public static TextView torqueValue;
    public static ArrayList<ModelExercise> movementList = new ArrayList<ModelExercise>();
    public static Cursor cursorMovement;
    public static int currentPosition = 0;


    public static TextView  movement_no_session_control;

    public static TextView  given_exercise_type;
    public static TextView  first_angle_session_control;
    public static TextView  second_angle_session_control;
    public static TextView  third_angle_session_control;
    public static TextView  given_repetition_session_control;

    public static TextView  first_movement_type_session_control;
    public static TextView  second_movement_type_session_control;
    public static TextView  first_movement_speed_session_control;
    public static TextView  second_movement_speed_session_control;
    public static TextView  first_movement_assistance_session_control;
    public static TextView  second_movement_assistance_session_control;
    public static TextView  first_movement_hold_time_session_control;
    public static TextView  second_movement_hold_time_session_control;

    public static Context mContext;
    public static View viewOfNextMovement;
    public static View viewOfAllExercises;


    public static CardView second_movement_assistance_session_control_card;
    public static LinearLayout second_hold_time_card;
    public static TextView second_movement_parameter_assistance_and_resistsnce;

    public static CardView first_movement_assistance_session_control_card;
    public static LinearLayout first_hold_time_card;
    public static TextView first_movement_parameters_assistance_and_resistance;

    public static String dbpath = "";

    public static Boolean isClickable = true;

    public static LineChart chart = null;
    public static KdGaugeView speedMeter = null;

    public static XAxis xAxis =null;
    public static YAxis yAxis = null;
}
