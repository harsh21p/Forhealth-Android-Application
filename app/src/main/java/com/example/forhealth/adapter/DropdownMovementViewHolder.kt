package com.example.forhealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.GuestMode
import com.example.forhealth.datamodel.ModelMovement

internal class DropdownMovementViewHolder (private var List: List<ModelMovement>, private val listener: GuestMode) :
    RecyclerView.Adapter<DropdownMovementViewHolder.MyViewHolder>() {
    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.clickListenerMovement(position)
            }
        }
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.dropdown_movement_view, parent, false)
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
        var movementName: TextView = itemView.findViewById(R.id.movement_text)
        fun bind(position: Int) {
            val recyclerViewModel = List[position]
            movementName.text = recyclerViewModel.movement
        }
    }
}
