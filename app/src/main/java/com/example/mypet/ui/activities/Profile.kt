package com.example.mypet.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*


class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var ownerId: String
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var photoRef: StorageReference
    val taskMap: MutableMap<String, Any> = HashMap()
    private lateinit var photo: CircleImageView
    private lateinit var saveBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val name = findViewById<EditText>(R.id.nameET)
        val breed = findViewById<EditText>(R.id.breedET)
        val dob = findViewById<AppCompatButton>(R.id.dobBtn)
        saveBtn = findViewById<AppCompatButton>(R.id.saveButton)
        val photoBtn = findViewById<AppCompatImageView>(R.id.editPhotoBtn)
        photo = findViewById(R.id.photoIV)
        val constraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        val actionBar: ActionBar? = supportActionBar
        actionBar.apply {
            title = "Profile"
        }


        auth = FirebaseAuth.getInstance()
        ownerId = auth.currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId)


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameSnapshot = snapshot.child("name").value
                val breedSnapshot = snapshot.child("breed").value
                val dobSnapshot = snapshot.child("dateOfBirth").value
                val photoSnapshot = snapshot.child("photo").value

                Log.d("SNAPSHOT", nameSnapshot.toString())
                name.setText(nameSnapshot.toString())
                breed.setText(breedSnapshot.toString())
                dob.text = dobSnapshot.toString()
//                Glide.with(applicationContext)
//                    .load(Drawable.)
//                    .into(photo);3
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        // set birthday
        val calendar = Calendar.getInstance()

        val cDay = calendar.get(Calendar.DAY_OF_MONTH)
        val cMonth = calendar.get(Calendar.MONTH)
        val cYear = Year.now().value

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar.time)
            dob.text = dateStr
            taskMap["dateOfBirth"] = dateStr
            reference.updateChildren(taskMap)
        }


        dob.setOnClickListener {
            DatePickerDialog(this, datePicker, cYear, cMonth, cDay).show()
        }

        saveBtn.setOnClickListener {
            taskMap["name"] = name.text.toString()
            taskMap["breed"] = breed.text.toString()
            reference.updateChildren(taskMap)
        }

        photoBtn.setOnClickListener {
            openGalleryForImage()
        }

        constraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE) {
            photo.setImageURI(data?.data)

            taskMap["photo"] = data?.data.toString()
            reference.updateChildren(taskMap)

        }
    }

}