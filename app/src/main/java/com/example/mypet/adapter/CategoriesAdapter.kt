package com.example.mypet.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mypet.R
import com.example.mypet.model.Category

class CategoriesAdapter(var context: Context, var categories: ArrayList<Category>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return categories.size
    }

    override fun getItem(position: Int): Any {
        return categories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.category_item, null)
        val image = view.findViewById<ImageView>(R.id.categoryIV)
        val name = view.findViewById<TextView>(R.id.categoryTV)

        val category = categories[position]
        image.setImageResource(category.image)
        name.text = category.name

        return view
    }


}