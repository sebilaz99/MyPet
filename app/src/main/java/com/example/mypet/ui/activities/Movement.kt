package com.example.mypet.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.MainActivity
import com.example.mypet.R

class Movement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        supportActionBar?.title = "Movement"

        val rv = findViewById<RecyclerView>(R.id.movementRV)
        val bottomConstraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)



        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}