package com.example.forhealth.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.ExistingUsers
import com.example.forhealth.datamodel.ModelExistingUsers

internal class ExistingUsersViewHolder (private var List: List<ModelExistingUsers>, private val listener: ExistingUsers) :
    RecyclerView.Adapter<ExistingUsersViewHolder.MyViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.existing_users_layout, parent, false)
            return View1ViewHolder(itemView)
        }
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.existing_users_add_button_layout, parent, false)
        return View2ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (List[position].viewType === VIEW_TYPE_ONE) {
            (holder as View1ViewHolder).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }


    }
    override fun getItemCount(): Int {
        return List.size
    }

    private inner class View1ViewHolder(itemView: View) :
        MyViewHolder(itemView) {
        var nameOfDoctor: TextView = itemView.findViewById(R.id.existing_user_name)
        var profileOfDoctor: ImageView = itemView.findViewById(R.id.existing_user_profile)
        var deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        fun bind(position: Int) {
            val recyclerViewModel = List[position]

            deleteButton.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            })


            nameOfDoctor.text = recyclerViewModel.nameOfDoctor
            if(recyclerViewModel.avatarOfDoctor == 0){
                profileOfDoctor.setImageDrawable(
                    ContextCompat.getDrawable(listener,
                    R.drawable.avatar_male
                ));
            }
            else{
                if(recyclerViewModel.avatarOfDoctor == 1){
                    profileOfDoctor.setImageDrawable(
                        ContextCompat.getDrawable(listener,
                            R.drawable.avatar_female_a
                        ));
                }else{
                    profileOfDoctor.setImageDrawable(
                        ContextCompat.getDrawable(listener,
                            R.drawable.avatar_female_b
                        ));
                }
            }

        }
    }

    private inner class View2ViewHolder(itemView: View) :
        MyViewHolder(itemView) {
        fun bind(position: Int) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return List[position].viewType
    }
}
