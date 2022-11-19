package com.example.forhealth.dagger


import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import androidx.cardview.widget.CardView
import com.google.android.material.slider.Slider


interface Services {

    fun hideKeyboard(mContext:Context)

    fun comingSoonDialogBox(view: View,context:Context)

    fun avatarDialogBox(view: View,context:Context)

    fun avatarDialogBoxForPatient(view: View,context: Context)

    fun setClickForSideBar(hamburger: ImageView, controls: CardView, sidebar: CardView, all_controls_card: CardView,mContext:Context)

    fun findPairedDevices()

    fun setClickForControls(view: View, sidebar: CardView, all_controls_card: CardView, controls: CardView, control_shutdown: CardView, control_reset: CardView
                            , control_set_home: CardView, control_close: CardView, control_direction_clockwise: CardView, control_direction_anticlockwise: CardView, control_brake_state: Switch, control_refresh: CardView,
                            context: Context,speed_slider:Slider
    )

    fun dialogueBoxShutdown(view: View, all_controls_card: CardView,context:Context)

    fun textDilog(view: View,context: Context)

    fun setupBT(address: String)

    fun messageReceived(readData: String)

    fun sendMessage(msg:String)

    fun stopChat()

    fun onItemClick(position: Int)

    fun bluetoothInit(
        controls: CardView,
        sidebar: CardView,
        all_controls_card: CardView,
        mContext: Context
    )
}