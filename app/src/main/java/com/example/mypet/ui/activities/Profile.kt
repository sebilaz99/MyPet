package com.example.mypet.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.ProfileItem
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
    private lateinit var profileReference: DatabaseReference
    private val taskMap: MutableMap<String, Any> = HashMap()
    private lateinit var photo: CircleImageView
    private lateinit var saveBtn: ConstraintLayout
    private lateinit var profileItemList: ArrayList<ProfileItem>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val name = findViewById<TextView>(R.id.nameTV)
        val breed = findViewById<TextView>(R.id.breedTV)
        val sex = findViewById<TextView>(R.id.genderTV)
        val dob = findViewById<AppCompatButton>(R.id.dobBtn)
        val toyDog = findViewById<ImageView>(R.id.toyDogIV)
        val smallDog = findViewById<ImageView>(R.id.smallDogIV)
        val mediumDog = findViewById<ImageView>(R.id.mediumDogIV)
        val largeDog = findViewById<ImageView>(R.id.largeDogIV)
        saveBtn = findViewById(R.id.saveButton)
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


        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameSnapshot = snapshot.child("name").value
                val breedSnapshot = snapshot.child("breed").value
                val dobSnapshot = snapshot.child("dateOfBirth").value
                val photoSnapshot = snapshot.child("photo").value
                val sexSnapshot = snapshot.child("sex").value

                Log.d("SNAPSHOT", nameSnapshot.toString())
                name.text = nameSnapshot.toString()
                breed.text = breedSnapshot.toString()
                Glide.with(applicationContext)
                    .load(photoSnapshot)
                    .placeholder(R.drawable.dog_placeholder)
                    .into(photo)

                when (sexSnapshot.toString()) {
                    "M" -> sex.text = "Male"
                    "F" -> sex.text = "Female"
                }
                dob.text = dobSnapshot.toString()

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
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            uploadToStorage()
        }

        photoBtn.setOnClickListener {
            openGalleryForImage()
        }

        constraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        profileItemList = arrayListOf()

        profileReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("profile")


        toyDog.setOnClickListener {
            Toast.makeText(this, "size: toy", Toast.LENGTH_LONG).show()
            val size = "toy"
            val profileItem = ProfileItem(size)
            profileReference.setValue(profileItem)
        }

        smallDog.setOnClickListener {
            Toast.makeText(this, "size: small", Toast.LENGTH_LONG).show()
            val size = "small"
            val profileItem = ProfileItem(size)
            profileReference.setValue(profileItem)
        }

        mediumDog.setOnClickListener {
            Toast.makeText(this, "size: medium", Toast.LENGTH_LONG).show()
            val size = "medium"
            val profileItem = ProfileItem(size)
            profileReference.setValue(profileItem)
        }

        largeDog.setOnClickListener {
            Toast.makeText(this, "size: large", Toast.LENGTH_LONG).show()
            val size = "large"
            val profileItem = ProfileItem(size)
            profileReference.setValue(profileItem)
        }

        profileReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot.child("size").value) {
                    "toy" -> {
                        toyDog.setImageResource(R.drawable.white_dog)
                        smallDog.setImageResource(R.drawable.dog_size)
                        mediumDog.setImageResource(R.drawable.dog_size)
                        largeDog.setImageResource(R.drawable.dog_size)
                    }
                    "small" -> {
                        toyDog.setImageResource(R.drawable.dog_size)
                        smallDog.setImageResource(R.drawable.white_dog)
                        mediumDog.setImageResource(R.drawable.dog_size)
                        largeDog.setImageResource(R.drawable.dog_size)
                    }
                    "medium" -> {
                        toyDog.setImageResource(R.drawable.dog_size)
                        mediumDog.setImageResource(R.drawable.white_dog)
                        smallDog.setImageResource(R.drawable.dog_size)
                        largeDog.setImageResource(R.drawable.dog_size)
                    }
                    "large" -> {
                        toyDog.setImageResource(R.drawable.dog_size)
                        largeDog.setImageResource(R.drawable.white_dog)
                        smallDog.setImageResource(R.drawable.dog_size)
                        mediumDog.setImageResource(R.drawable.dog_size)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE) {

            imageUri = data?.data!!
            photo.setImageURI(imageUri)

            taskMap["photo"] = data.data.toString()
            reference.updateChildren(taskMap)
        }
    }

    private fun uploadToStorage() {
        photoRef = storageRef.child("photos/$ownerId")

        photoRef.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(this, "Image has been successfully updated!", Toast.LENGTH_SHORT).show()
            taskMap["photo"] = imageUri
            reference.updateChildren(taskMap)
            Glide.with(applicationContext)
                .load(imageUri)
                .into(photo)
        }
    }

}