package com.example.mypet.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mypet.R
import com.example.mypet.model.ServiceItem
import com.example.mypet.ui.activities.Map

class ServicesAdapter(private var list: ArrayList<ServiceItem>) :
    RecyclerView.Adapter<ServicesAdapter.ServicesViewHolder>() {


    inner class ServicesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.serviceNameTV)
        val image: ImageView = itemView.findViewById(R.id.serviceImageIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.service_item, parent, false)
        return ServicesViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val current = list[position]

        holder.name.text = current.name
        Glide.with(holder.image.context)
            .load(current.image)
            .into(holder.image)

        holder.itemView.setOnLongClickListener {
            Toast.makeText(it.context, holder.name.text.toString(), Toast.LENGTH_SHORT).show()
            true
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, Map::class.java)
            intent.putExtra("type", current.name)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}