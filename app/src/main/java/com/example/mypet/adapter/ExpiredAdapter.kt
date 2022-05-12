package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.model.ExpiredItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar


class ExpiredAdapter(items: FirebaseRecyclerOptions<ExpiredItem>) :
    FirebaseRecyclerAdapter<ExpiredItem, ExpiredAdapter.ExpiredViewHolder>(items) {


    inner class ExpiredViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.expNameTV)
        val type: TextView = itemView.findViewById(R.id.expTypeTV)
        val date: TextView = itemView.findViewById(R.id.expDateTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpiredViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expired_item, parent, false)
        return ExpiredViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpiredViewHolder, position: Int, model: ExpiredItem) {
        holder.name.text = model.name
        holder.type.text = model.type
        holder.date.text = model.date

        holder.itemView.setOnLongClickListener {
            Snackbar.make(it, "Are you sure you want to delete this?", Snackbar.LENGTH_LONG)
                .setAction("YES") {
                    getRef(position).removeValue()
                }.show()
            true
        }
    }

}
