package com.example.forhealth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.widget.Switch;
import android.widget.TextView;

import com.example.forhealth.adapter.PairedDevicesViewHolder;
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
    public static String selectedSessionName = null;
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

    public static LineChart chart = null;
    public static KdGaugeView speedMeter = null;

    public static XAxis xAxis =null;
    public static YAxis yAxis = null;
}
