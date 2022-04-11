package com.example.mypet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate

class Food : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ownerId: String
    private lateinit var reference: DatabaseReference
    private lateinit var petReference: DatabaseReference
    private lateinit var profileReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        supportActionBar?.title = "Food"

        val txt = findViewById<TextView>(R.id.txt)
        val bowl = findViewById<ImageView>(R.id.bowlIV)
        val feedConstraint = findViewById<ConstraintLayout>(R.id.feedConstraint)
        val bottomConstraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)

        val toy = findViewById<ImageView>(R.id.toyIV)
        val small = findViewById<ImageView>(R.id.smallIV)
        val medium = findViewById<ImageView>(R.id.mediumIV)
        val large = findViewById<ImageView>(R.id.largeIV)

        toy.visibility = View.INVISIBLE
        small.visibility = View.INVISIBLE
        medium.visibility = View.INVISIBLE
        large.visibility = View.INVISIBLE


        auth = FirebaseAuth.getInstance()
        ownerId = auth.currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("food")

        petReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId)


        val currentDate = LocalDate.now().toString()
        val currentYear = currentDate.subSequence(0, 4).toString()
        val currentMonth = currentDate.subSequence(5, 7).toString()
        val currentDay = currentDate.subSequence(8, 10).toString()
        val currentDateStringFormat = "$currentDay-$currentMonth-$currentYear"


        petReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameSnap = snapshot.child("name").value
                txt.text = "Feed $nameSnap Now!"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        profileReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("profile")

        profileReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot.child("size").value) {
                    "toy" -> toy.visibility = View.VISIBLE
                    "small" -> small.visibility = View.VISIBLE
                    "medium" -> medium.visibility = View.VISIBLE
                    "large" -> large.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timestampSnap = snapshot.child("timestamp").value

                if (timestampSnap.toString() == currentDateStringFormat) {
                    bowl.setImageResource(R.drawable.dog_food_checked)
                    txt.text = "Your dog is fed!"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        feedConstraint.setOnClickListener {

            val foodIem = FoodItem(currentDateStringFormat)
            reference.setValue(foodIem)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timestampSnap = snapshot.child("timestamp").value

                    when (timestampSnap.toString()) {
                        currentDateStringFormat -> Toast.makeText(
                            this@Food,
                            "You already fed your pet today!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            bowl.setImageResource(R.drawable.dog_food_checked)
        }


        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}

