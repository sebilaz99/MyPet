package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.model.Vaccine
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar


class VaccinesAdapter(items: FirebaseRecyclerOptions<Vaccine>) :
    FirebaseRecyclerAdapter<Vaccine, VaccinesAdapter.VaccineViewHolder>(items) {

    inner class VaccineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brand: TextView = itemView.findViewById(R.id.brandTV)
        val startDate: TextView = itemView.findViewById(R.id.startDateTV)
        val endDate: TextView = itemView.findViewById(R.id.endDateTV)
        val vaxType: TextView = itemView.findViewById(R.id.typeTV)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteItemIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vaccine_item, parent, false)
        return VaccineViewHolder(view)
    }

    override fun onBindViewHolder(holder: VaccineViewHolder, position: Int, model: Vaccine) {
        holder.brand.text = model.brand
        holder.startDate.text = model.date
        holder.endDate.text = model.exp
        holder.vaxType.text = model.type

        holder.itemView.setOnLongClickListener {
            holder.deleteBtn.visibility = View.VISIBLE
            true
        }

        holder.deleteBtn.setOnClickListener {
            Snackbar.make(it, "Are you sure you want to delete this?", Snackbar.LENGTH_LONG)
                .setAction("YES") {
                    getRef(position).removeValue()
                }.show()
            Toast.makeText(it.context, "Vaccine has been deleted!", Toast.LENGTH_SHORT).show()
        }
    }
}
