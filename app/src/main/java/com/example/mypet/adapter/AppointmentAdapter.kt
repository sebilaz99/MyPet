package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.model.AppointmentModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class AppointmentAdapter(items: FirebaseRecyclerOptions<AppointmentModel>) :
    FirebaseRecyclerAdapter<AppointmentModel, AppointmentAdapter.AppointmentViewHolder>(items) {


    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.appTypeTV)
        val date: TextView = itemView.findViewById(R.id.appDateTV)
        val time: TextView = itemView.findViewById(R.id.appTimeTV)
        val delBtn: ImageView = itemView.findViewById(R.id.deleteItemIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AppointmentViewHolder,
        position: Int,
        model: AppointmentModel
    ) {
        holder.type.text = model.type
        holder.date.text = model.date
        holder.time.text = model.time

        holder.itemView.setOnLongClickListener {
            holder.delBtn.visibility = View.VISIBLE
            true
        }

        holder.delBtn.setOnClickListener {
            getRef(position).removeValue()
            Toast.makeText(it.context, "Appointment has been deleted!", Toast.LENGTH_SHORT).show()
        }
    }

}
