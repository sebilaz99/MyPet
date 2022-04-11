package com.example.mypet.ui.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.adapter.AppointmentAdapter
import com.example.mypet.model.AppointmentModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

class Appointment : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var appReference: DatabaseReference
    private lateinit var ownerId: String
    private lateinit var appointmentList: ArrayList<AppointmentModel>
    lateinit var adapter: ArrayAdapter<String>
    private lateinit var appRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        val appType = intent.getStringExtra("type")
        val appointmentType = "$appType Appointments"

        supportActionBar?.title = appointmentType

        val bottomConstraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)
        val dateBtn = findViewById<Button>(R.id.dateBtn)
        val timeBtn = findViewById<Button>(R.id.timeBtn)
        val addBtn = findViewById<AppCompatButton>(R.id.addAppointmentBtn)
        appRV = findViewById(R.id.appointmentRV)

        auth = FirebaseAuth.getInstance()
        ownerId = auth.currentUser!!.uid
        appReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("appointments")


        val calendar = Calendar.getInstance()
        val cDay = calendar.get(Calendar.DAY_OF_MONTH)
        val cMonth = calendar.get(Calendar.MONTH)
        val cYear = Year.now().value

        val cHour = calendar.get(Calendar.HOUR_OF_DAY)
        val cMinute = calendar.get(Calendar.MINUTE)

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar.time)
            dateBtn.text = dateStr
            dateBtn.textSize = 10F
        }

        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            val format = "HH:MM"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val hourStr = sdf.format(calendar.time)
            timeBtn.text = hourStr
            timeBtn.textSize = 10F
        }


        timeBtn.setOnClickListener {
            TimePickerDialog(it.context, timePicker, cHour, cMinute, true).show()
        }

        dateBtn.setOnClickListener {
            DatePickerDialog(it.context, datePicker, cYear, cMonth, cDay).show()
        }

        addBtn.setOnClickListener {
            val appointment =
                AppointmentModel(appType!!, dateBtn.text.toString(), timeBtn.text.toString())
            appReference.push().setValue(appointment)
            Toast.makeText(this, "New appointment has been added!", Toast.LENGTH_SHORT).show()
        }

        bottomConstraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        appRV.layoutManager = LinearLayoutManager(this)
        appRV.setHasFixedSize(true)
        appointmentList = arrayListOf()

        fetchAppointments()
    }

    private fun fetchAppointments() {
        val options = FirebaseRecyclerOptions.Builder<AppointmentModel>()
            .setQuery(appReference, AppointmentModel::class.java)
            .setLifecycleOwner(this)
            .build()

        appReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val typeSnap = item.child("type").value.toString()
                        val dateSnap = item.child("date").value.toString()
                        val timeSnap = item.child("time").value.toString()
                        val appointment = AppointmentModel(typeSnap, dateSnap, timeSnap)
                        appointmentList.add(appointment)
                    }

                    appRV.adapter = AppointmentAdapter(options)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}