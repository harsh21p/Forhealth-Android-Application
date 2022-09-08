package com.example.forhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.bluetooth.Bluetooth
import com.example.forhealth.datamodel.ModelPairedDevices

internal class PairedDevicesViewHolder (private var List: List<ModelPairedDevices>, private val listener: Context,private val progressBar:ProgressBar,private val sidebar:CardView,private val controls:CardView) :
    RecyclerView.Adapter<PairedDevicesViewHolder.MyViewHolder>() {
    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                val bluetooth = Bluetooth(listener)
                bluetooth.onItemClick(position,listener,progressBar,sidebar,controls)
            }
        }
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bluetooth_paired_devices_layout, parent, false)
        return View1ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        (holder as View1ViewHolder).bind(position)
    }
    override fun getItemCount(): Int {
        return List.size
    }
    private inner class View1ViewHolder(itemView: View) :
        MyViewHolder(itemView) {
        var deviceName: TextView = itemView.findViewById(R.id.bluetooth_device_name)
        var deviceId: TextView = itemView.findViewById(R.id.bluetooth_device_id)
        fun bind(position: Int) {
            val recyclerViewModel = List[position]
            deviceName.text = recyclerViewModel.deviceName
            deviceId.text = recyclerViewModel.deviceId
        }
    }
}
