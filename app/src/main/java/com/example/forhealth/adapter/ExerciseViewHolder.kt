package com.example.forhealth.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.forhealth.R
import com.example.forhealth.activity.SessionInformation
import com.example.forhealth.datamodel.ModelExercise

internal class ExerciseViewHolder (private var List: List<ModelExercise>, private val listener: SessionInformation) :
    RecyclerView.Adapter<ExerciseViewHolder.MyViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    internal open inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.onExerciseClick(position)
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movement_view_layout, parent, false)
            return View1ViewHolder(itemView)
        }
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_movement_layout, parent, false)
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



        var movementNo: TextView = itemView.findViewById(R.id.movement_no)
        var exerciseType: TextView = itemView.findViewById(R.id.exercise_type)
        var firstAngle: TextView = itemView.findViewById(R.id.first_angle)
        var secondAngle: TextView = itemView.findViewById(R.id.second_angle)
        var thirdAngle: TextView = itemView.findViewById(R.id.third_angle)
        var repetation: TextView = itemView.findViewById(R.id.repetation)
        var firstMovementSpeed: TextView = itemView.findViewById(R.id.first_movement_speed)
        var firstMovementAssistance: TextView = itemView.findViewById(R.id.first_movement_assistance)
        var firstMovementResistance: TextView = itemView.findViewById(R.id.first_movement_resistance)
        var firstMovementHoldTime: TextView = itemView.findViewById(R.id.first_movement_hold_time)
        var secondMovementSpeed: TextView = itemView.findViewById(R.id.second_movement_speed)
        var secondMovementAssistance: TextView = itemView.findViewById(R.id.second_movement_assistance)
        var secondMovementResistance: TextView = itemView.findViewById(R.id.second_movement_resistance)
        var secondMovementHoldTime: TextView = itemView.findViewById(R.id.second_movement_hold_time)
        var firstMovementType:TextView = itemView.findViewById(R.id.first_movement_type)
        var secondMovementType:TextView = itemView.findViewById(R.id.second_movement_type)

        var editButton:ImageView = itemView.findViewById(R.id.edit_button)
        var deleteButton:ImageView = itemView.findViewById(R.id.delete_button)

        fun bind(position: Int) {
            val recyclerViewModel = List[position]

            if(recyclerViewModel.firstMovementType=="PASSIVE"){
                 firstMovementResistance.text="NA"
              firstMovementAssistance.text="NA"
                firstMovementHoldTime.text="NA"
            }
            else{
                if(recyclerViewModel.firstMovementType=="AR ( ISOMETRIC )"){
                  firstMovementAssistance.text="NA"
                     firstMovementResistance.text=recyclerViewModel.firstMovementResistance
                    firstMovementHoldTime.text=recyclerViewModel.firstMovementHoldTime
                }
                else{
                  firstMovementAssistance.text=recyclerViewModel.firstMovementAssistance
                     firstMovementResistance.text="NA"
                    firstMovementHoldTime.text="NA"
                }
            }

            if(recyclerViewModel.secondMovementType=="PASSIVE"){
                secondMovementResistance.text="NA"
                secondMovementAssistance.text="NA"
                secondMovementHoldTime.text="NA"
            } else{
                if(recyclerViewModel.secondMovementType == "AR ( ISOMETRIC )"){
                    secondMovementAssistance.text="NA"
                    secondMovementResistance.text=recyclerViewModel.secondMovementResistance
                    secondMovementHoldTime.text=recyclerViewModel.secondMovementHoldTime
                }
                else{
                    secondMovementResistance.text="NA"
                    secondMovementHoldTime.text="NA"
                    secondMovementAssistance.text=recyclerViewModel.secondMovementAssistance
                }
            }

            editButton.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION) {
                listener.editButton(position)
            }

            })


            deleteButton.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                if(position!= RecyclerView.NO_POSITION) {
                    listener.deleteButton(position)
                }

            })

            firstMovementType.text = recyclerViewModel.firstMovementType
            secondMovementType.text = recyclerViewModel.secondMovementType
            movementNo.text = recyclerViewModel.movementNo
            exerciseType.text = recyclerViewModel.exerciseType
            firstAngle.text = recyclerViewModel.firstAngle
            secondAngle.text = recyclerViewModel.secondAngle
            thirdAngle.text = recyclerViewModel.thirdAngle
            repetation.text = recyclerViewModel.repetition
            firstMovementSpeed.text = recyclerViewModel.firstMovementSpeed
            secondMovementSpeed.text = recyclerViewModel.secondMovementSpeed
            
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
