package com.example.mypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.R
import com.example.mypet.model.Medication
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class MedicationAdapter(items: FirebaseRecyclerOptions<Medication>) :
    FirebaseRecyclerAdapter<Medication, MedicationAdapter.MedicationViewHolder>(items) {


    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brand: TextView = itemView.findViewById(R.id.medBrandTV)
        val startDate: TextView = itemView.findViewById(R.id.startDateTV)
        val endDate: TextView = itemView.findViewById(R.id.endDateTV)
        val medType: TextView = itemView.findViewById(R.id.medTypeTV)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteItemIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.medication_item, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int, model: Medication) {
        holder.brand.text = model.brand
        holder.startDate.text = model.date
        holder.endDate.text = model.exp
        holder.medType.text = model.type

        holder.itemView.setOnLongClickListener {
            holder.deleteBtn.visibility = View.VISIBLE
            true

        }

        holder.deleteBtn.setOnClickListener {
            getRef(position).removeValue()
            Toast.makeText(it.context, "Medication has been deleted!", Toast.LENGTH_SHORT).show()
        }
    }

}
