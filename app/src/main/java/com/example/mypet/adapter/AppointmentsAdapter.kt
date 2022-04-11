package com.example.mypet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mypet.R
import com.example.mypet.model.AppointmentItem

class AppointmentsAdapter(private var list: ArrayList<AppointmentItem>) :
    RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder>() {


    inner class AppointmentsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.serviceNameTV)
        val image: ImageView = itemView.findViewById(R.id.serviceImageIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.service_item, parent, false)
        return AppointmentsViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: AppointmentsViewHolder, position: Int) {
        val current = list[position]

        holder.name.text = current.name
        Glide.with(holder.image.context)
            .load(current.image)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, holder.name.text.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}