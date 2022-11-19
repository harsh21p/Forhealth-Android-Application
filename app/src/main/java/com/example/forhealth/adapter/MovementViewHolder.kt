package com.example.forhealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.SessionControlPanel
import com.example.forhealth.datamodel.ModelExercise

internal class MovementViewHolder (private var mList: List<ModelExercise>, private val listener: SessionControlPanel) :
    RecyclerView.Adapter<MovementViewHolder.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.onMovementClick(position)
            }
        }
        var existingSessionText: TextView = view.findViewById(R.id.existing_movement_text)
    }


    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movements_list_layout_control_panal, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val modelClone = mList[position]
        holder.existingSessionText.text = modelClone.movementNo

    }
    override fun getItemCount(): Int {
        return mList.size
    }
}