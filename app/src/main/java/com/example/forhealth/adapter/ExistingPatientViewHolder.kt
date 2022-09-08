package com.example.forhealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.ExistingPatient
import com.example.forhealth.datamodel.ModelExistingPatient
import kotlinx.android.synthetic.main.existing_patient_view.view.*

internal class ExistingPatientViewHolder (private var List: List<ModelExistingPatient>, private val listener: ExistingPatient) :
    RecyclerView.Adapter<ExistingPatientViewHolder.MyViewHolder>() {
    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            itemView.view_button.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.onClickPatient(position)
            }
        }
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.existing_patient_view, parent, false)
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
        var patientName: TextView = itemView.findViewById(R.id.existing_user_name_recycler)
        var patientAge: TextView = itemView.findViewById(R.id.existing_user_age_recycler)
        var patientContact: TextView = itemView.findViewById(R.id.existing_user_phone_recycler)
        var patientDate: TextView = itemView.findViewById(R.id.existing_user_date_recycler)
        var patientAvatar: ImageView = itemView.findViewById(R.id.profile_existing_user_recycler)
        var viewButton: CardView = itemView.findViewById(R.id.view_button)

        fun bind(position: Int) {
            val recyclerViewModel = List[position]
            patientName.text = recyclerViewModel.name
            patientAge.text = recyclerViewModel.age.toString()
            patientDate.text = recyclerViewModel.date
            patientContact.text = recyclerViewModel.contact

            if(recyclerViewModel.image == 0){
                patientAvatar.setImageDrawable(
                    ContextCompat.getDrawable(listener,
                        R.drawable.male_avatar_patient_a
                    ));
            }else{
                if(recyclerViewModel.image == 1){
                    patientAvatar.setImageDrawable(
                        ContextCompat.getDrawable(listener,
                            R.drawable.male_avatar_patient_b
                        ));
                }else{
                    if(recyclerViewModel.image == 2){
                        patientAvatar.setImageDrawable(
                            ContextCompat.getDrawable(listener,
                                R.drawable.female_avatar_patient_a
                            ));
                    }else{
                        patientAvatar.setImageDrawable(
                            ContextCompat.getDrawable(listener,
                                R.drawable.female_avatar_patient_b
                            ));
                    }
                }
            }
        }
    }
}
