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

internal class DropdownSessionViewHolder (private var List: ArrayList<ModelMovement>, private val listener: GuestMode) :
    RecyclerView.Adapter<DropdownSessionViewHolder.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.onSessionClick(position)
            }
        }
        var sessionName: TextView = view.findViewById(R.id.session_name_text)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_dropdown_view, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sessionName = List[position]
        holder.sessionName.text = sessionName.movement

    }
    override fun getItemCount(): Int {
        return List.size
    }
}
