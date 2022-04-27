package com.example.mypet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.FactModel
import com.example.mypet.model.RandomImageModel
import com.example.mypet.network.Networking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FunFacts : AppCompatActivity() {

    lateinit var factTxt: TextView
    lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fun_facts)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        val constraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)

        supportActionBar?.title = "Fun Facts"

        fetchRandomFact()
        fetchRandomImage()

        constraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun fetchRandomFact() {
        val call: Call<FactModel> = Networking.factInstance.getFact()
        call.enqueue(object : Callback<FactModel> {
            override fun onResponse(
                call: Call<FactModel>,
                response: Response<FactModel>
            ) {
                val facts: String = response.body()!!.facts[0]
                factTxt = findViewById(R.id.factContentTV)
                factTxt.text = facts

            }

            override fun onFailure(call: Call<FactModel>, t: Throwable?) {
                factTxt = findViewById(R.id.factContentTV)
                factTxt.text = "Fetching error"
                if (t != null) {
                    t.message?.let { Log.d("FACT", it) }
                }
            }
        })
    }


    private fun fetchRandomImage() {
        val call: Call<RandomImageModel> = Networking.imageInstance.getRandomImage()
        call.enqueue(object : Callback<RandomImageModel> {
            override fun onResponse(
                call: Call<RandomImageModel>,
                response: Response<RandomImageModel>
            ) {
                val url: String = response.body()!!.url
                image = findViewById(R.id.randomIV)
                Glide.with(this@FunFacts)
                    .load(url)
                    .placeholder(R.drawable.dog_api_placeholder)
                    .into(image)

            }

            override fun onFailure(call: Call<RandomImageModel>, t: Throwable?) {
                if (t != null) {
                    t.message?.let { Log.d("FACT", it) }
                }
            }
        })
    }
}