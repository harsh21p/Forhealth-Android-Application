package com.example.forhealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.PatientProfilePage
import com.example.forhealth.datamodel.ModelScheduledSession

internal class SessionsViewHolder (private var List: List<ModelScheduledSession>, private val listener: PatientProfilePage) :
    RecyclerView.Adapter<SessionsViewHolder.MyViewHolder>() {
    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {

                listener.onSessionClick(position)

            }
        }
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.show_sessions, parent, false)
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
        var sessionName: TextView = itemView.findViewById<TextView>(R.id.session_name_session)
        var deleteButton: ImageView = itemView.findViewById<ImageView>(R.id.delete_button)

        fun bind(position: Int) {
            val recyclerViewModel = List[position]
            sessionName.text = recyclerViewModel.sessionName

            deleteButton.setOnClickListener(View.OnClickListener {

                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION) {
                    listener.deleteButton(position)
                }

            })
        }
    }
}