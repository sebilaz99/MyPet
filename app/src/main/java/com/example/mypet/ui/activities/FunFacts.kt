package com.example.mypet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.FactModel
import com.example.mypet.network.Networking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FunFacts : AppCompatActivity() {

    lateinit var factTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fun_facts)

        val constraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)

        supportActionBar?.title = "Fun Facts"

        fetchRandomFact()

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
}