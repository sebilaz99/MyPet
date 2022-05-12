package com.example.mypet.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class Food : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ownerId: String
    private lateinit var reference: DatabaseReference
    private lateinit var petReference: DatabaseReference
    private lateinit var profileReference: DatabaseReference
    private lateinit var calculatorReference: DatabaseReference
    private val taskMap: MutableMap<String, Any> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        supportActionBar?.title = "Food"

        val foodConstraint = findViewById<ImageView>(R.id.foodConstraint)
        val treatConstraint = findViewById<ImageView>(R.id.treatConstraint)
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


        calculatorReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("calculator")


        foodConstraint.setOnClickListener {
            val foodIem = FoodItem(currentDateStringFormat, "food")
            reference.child("food").setValue(foodIem)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timestampSnap = snapshot.child("timestamp").value

                    if (snapshot.child("type").value == "food") {

                        when (timestampSnap.toString()) {
                            currentDateStringFormat -> {
                                Toast.makeText(
                                    this@Food,
                                    "You already fed your pet today",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d("FOOD", "Already fed today")
                            }

                            else -> {
                                calculatorReference.child("food")
                                    .child(SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(Date()))
                                    .setValue("fed")
                                Log.d("FOOD", "First time fed today")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }


        treatConstraint.setOnClickListener {
            val foodIem = FoodItem(currentDateStringFormat, "treat")
            reference.child("treat").setValue(foodIem)

            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timestampSnap = snapshot.child("timestamp").value

                    if (snapshot.child("type").value == "treat") {

                        when (timestampSnap.toString()) {
                            currentDateStringFormat -> {
                                Toast.makeText(
                                    this@Food,
                                    "You already treated your pet today",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.d("TREAT", "Already treated today")
                            }

                            else -> {
                                calculatorReference.child("food")
                                    .child(SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(Date()))
                                    .setValue("fed")
                                Log.d("TREAT", "First time treated today")
                            }
                        }
                        val foodIem = FoodItem(currentDateStringFormat)
                        reference.setValue(foodIem)

                        reference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val timestampSnap = snapshot.child("timestamp").value

                                when (timestampSnap.toString()) {
                                    currentDateStringFormat -> {
                                        Toast.makeText(
                                            this@Food,
                                            "You already fed your pet today",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        Log.d("FOOD", "Already fed today")
                                    }

                                    else -> {
                                        calculatorReference.child("food")
                                            .child(
                                                SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(
                                                    Date()
                                                )
                                            )
                                            .setValue("fed")
                                        Log.d("FOOD", "First time fed today")
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}

