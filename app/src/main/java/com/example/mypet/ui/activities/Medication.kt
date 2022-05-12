package com.example.mypet.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.adapter.MedicationAdapter
import com.example.mypet.model.ExpiredItem
import com.example.mypet.model.Medication
import com.example.mypet.model.MedicationType
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.util.*

class Medication : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ownerId: String
    private lateinit var reference: DatabaseReference
    private lateinit var expReference: DatabaseReference
    private lateinit var rv: RecyclerView
    private lateinit var medicationList: ArrayList<Medication>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        supportActionBar?.title = "Medication"

        val addBtn = findViewById<AppCompatButton>(R.id.addMedBtn)
        val startDateBtn = findViewById<Button>(R.id.dateBtn)
        val endDateBtn = findViewById<Button>(R.id.expBtn)
        val typeACTV = findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteTextView)
        val bottomConstraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)


        val typesList = mutableListOf<String>()
        for (type in MedicationType.values()) {
            typesList.add(type.toString())
        }

        adapter = ArrayAdapter(this, R.layout.types_dropdown_item, typesList)
        typeACTV.setAdapter(adapter)

        rv = findViewById(R.id.medicationsRV)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        medicationList = arrayListOf()

        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        val cDay = calendar.get(Calendar.DAY_OF_MONTH)
        val cMonth = calendar.get(Calendar.MONTH)
        val cYear = Year.now().value

        val cDay2 = calendar2.get(Calendar.DAY_OF_MONTH)
        val cMonth2 = calendar2.get(Calendar.MONTH)
        val cYear2 = Year.now().value

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar.time)
            startDateBtn.text = dateStr
            startDateBtn.textSize = 8F
        }

        val datePicker2 = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar2.set(Calendar.YEAR, year)
            calendar2.set(Calendar.MONTH, month)
            calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar2.time)
            endDateBtn.text = dateStr
            endDateBtn.textSize = 8F
        }


        addBtn.setOnClickListener {
            val brand = findViewById<EditText>(R.id.brandET)
            val brandStr = brand.text.toString().trim()
            val type = typeACTV.text.toString()


            when {
                startDateBtn.text.toString() == "" -> {
                    Toast.makeText(this, "Choose the administration date!", Toast.LENGTH_LONG)
                        .show()
                }
                endDateBtn.text.toString() == "" -> {
                    Toast.makeText(this, "Choose the expiring date!", Toast.LENGTH_LONG).show()
                }
                else -> {

                    val currentDate = LocalDate.now().toString()
                    val currentYear = currentDate.subSequence(0, 4).toString()
                    val currentMonth = currentDate.subSequence(5, 7).toString()
                    val currentDay = currentDate.subSequence(8, 10).toString()
                    val currentDateStringFormat = "$currentDay-$currentMonth-$currentYear"
                    val currentDateNewFormat =
                        SimpleDateFormat("dd-MM-yyyy", Locale.UK).parse(currentDateStringFormat)
                    val expDate =
                        SimpleDateFormat("dd-MM-yyyy", Locale.UK).parse(endDateBtn.text.toString())
                    val expDateString = endDateBtn.text.toString()

                    expReference = FirebaseDatabase.getInstance().reference.child("Pet")
                        .child(ownerId).child("expired")


                    if (currentDateNewFormat.before(expDate)) {
                        Log.d("VAX DATE", "NOT EXPIRED")
                    } else {
                        val item = ExpiredItem("Medication", type, expDateString)
                        expReference.push().setValue(item)
                        Log.d("VAX DATE", "EXPIRED")
                    }


                    val med =
                        Medication(
                            brandStr,
                            startDateBtn.text.toString(),
                            endDateBtn.text.toString(),
                            type
                        )

                    reference = FirebaseDatabase.getInstance().reference.child("Pet")
                        .child(ownerId).child("medication")
                    reference.push().setValue(med)
                    Toast.makeText(this, "New medication has been added!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        startDateBtn.setOnClickListener {
            DatePickerDialog(it.context, datePicker, cYear, cMonth, cDay).show()
        }

        endDateBtn.setOnClickListener {
            DatePickerDialog(it.context, datePicker2, cYear2, cMonth2, cDay2).show()
        }

        fetchMedicationList()


        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun fetchMedicationList() {
        auth = FirebaseAuth.getInstance()
        ownerId = auth.currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("medication")


        val options = FirebaseRecyclerOptions.Builder<Medication>()
            .setQuery(reference, Medication::class.java)
            .setLifecycleOwner(this)
            .build()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val brand = item.child("brand").value.toString()
                        val adminDate = item.child("date").value.toString()
                        val expDate = item.child("exp").value.toString()
                        val type = item.child("type").value.toString()
                        val med = Medication(brand, adminDate, expDate, type)
                        medicationList.add(med)
                    }

                    rv.adapter = MedicationAdapter(options)
                }

                val foundTxt = findViewById<TextView>(R.id.foundText)

                if (snapshot.childrenCount.toInt() == 0) {
                    foundTxt.visibility = View.VISIBLE
                    rv.background =
                        resources.getDrawable((com.firebase.ui.auth.R.color.fui_transparent))
                } else {
                    foundTxt.visibility = View.INVISIBLE
                    rv.background =
                        resources.getDrawable((R.drawable.dog_care))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}