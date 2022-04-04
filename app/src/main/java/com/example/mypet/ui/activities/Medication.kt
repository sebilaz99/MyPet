package com.example.mypet.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.adapter.MedicationAdapter
import com.example.mypet.model.Medication
import com.example.mypet.model.MedicationType
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

class Medication : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ownerId: String
    private lateinit var reference: DatabaseReference
    private lateinit var rv: RecyclerView
    private lateinit var medicationList: ArrayList<Medication>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        supportActionBar?.title = "Medication"

        val addBtn = findViewById<AppCompatButton>(R.id.addMedBtn)
        val startDateBtn = findViewById<AppCompatButton>(R.id.dateBtn)
        val endDateBtn = findViewById<AppCompatButton>(R.id.expBtn)
        val typeACTV = findViewById<AutoCompleteTextView>(R.id.typeAutoCompleteTextView)
        val bottomConstraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)
        val middleConstraint = findViewById<ConstraintLayout>(R.id.middleConstraint)

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

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar.time)
            startDateBtn.text = dateStr
        }

        val datePicker2 = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar2.set(Calendar.YEAR, year)
            calendar2.set(Calendar.MONTH, month)
            calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar2.time)
            endDateBtn.text = dateStr
        }


        addBtn.setOnClickListener {
            val brand = findViewById<EditText>(R.id.brandET)
            val brandStr = brand.text.toString().trim()
            val type = typeACTV.text.toString()


            if (startDateBtn.text.toString() == "Administration Date") {
                startDateBtn.text = "??-??-????"
            }
            if (endDateBtn.text.toString() == "Expiring Date") {
                endDateBtn.text = "??-??-????"
            }


            val med =
                Medication(brandStr, startDateBtn.text.toString(), endDateBtn.text.toString(), type)

            reference = FirebaseDatabase.getInstance().reference.child("Pet")
                .child(ownerId).child("medication")
            reference.push().setValue(med)
            Toast.makeText(this, "New medication has been added!", Toast.LENGTH_SHORT).show()
        }

        startDateBtn.setOnClickListener {
            DatePickerDialog(it.context, datePicker, cYear, cMonth, cDay).show()
        }

        endDateBtn.setOnClickListener {
            DatePickerDialog(it.context, datePicker2, cYear2, cMonth2, cDay2).show()
        }

        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        middleConstraint.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        fetchMedicationList()

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

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}