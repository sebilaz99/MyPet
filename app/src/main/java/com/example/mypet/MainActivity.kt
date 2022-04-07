package com.example.mypet

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mypet.adapter.ExpiredAdapter
import com.example.mypet.adapter.ServicesAdapter
import com.example.mypet.adapter.SpacingDecorator
import com.example.mypet.login.Login
import com.example.mypet.model.ExpiredItem
import com.example.mypet.model.ServiceItem
import com.example.mypet.model.UserStatus
import com.example.mypet.ui.activities.FunFacts
import com.example.mypet.ui.activities.Medication
import com.example.mypet.ui.activities.Profile
import com.example.mypet.ui.activities.Vaccination
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var petReference: DatabaseReference
    private lateinit var expReference: DatabaseReference
    private var ownerId = ""
    private lateinit var expList: ArrayList<ExpiredItem>
    var xpLong by Delegates.notNull<Long>()
    val taskMap: MutableMap<String, Any> = HashMap()
    private lateinit var adapter: ServicesAdapter
    private var list: ArrayList<ServiceItem>? = null
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var photoRef: StorageReference

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.medium_green)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser!!

        ownerId = auth.currentUser!!.uid
        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("owner")

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val nv = findViewById<NavigationView>(R.id.navigationView)
        val constraint = findViewById<ConstraintLayout>(R.id.bottomConstraint)

        val header = nv.getHeaderView(0)

        val xpTV = header.findViewById<TextView>(R.id.xpTextView)
        val nameTV = header.findViewById<TextView>(R.id.petNameTextView)
        val image = header.findViewById<CircleImageView>(R.id.circleImageView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val progressBar = header.findViewById<ProgressBar>(R.id.progressBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nv.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profileItem -> {
                    startActivity(Intent(this, Profile::class.java))
                    Log.d("Nav", "Profile")
                }
                R.id.signOutItem -> {
                    val taskMap: MutableMap<String, Any> = HashMap()
                    taskMap["status"] = UserStatus.OFFLINE.toString()
                    reference.updateChildren(taskMap)
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                R.id.factsItem -> {
                    startActivity(Intent(this, FunFacts::class.java))
                    Log.d("Nav", "Facts")
                }
                R.id.vaccinesItem -> {
                    startActivity(Intent(this, Vaccination::class.java))
                    Log.d("Nav", "Vaccines")
                }
                R.id.medicationItem -> {
                    startActivity(Intent(this, Medication::class.java))
                    Log.d("Nav", "Medication")
                }
            }
            true
        }

        constraint.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val updateBtn = findViewById<ImageView>(R.id.updateScoreIV)

        updateBtn.setOnClickListener {
            taskMap["score"] = xpTV.text.toString()
            petReference.updateChildren(taskMap)
        }

        storageRef = FirebaseStorage.getInstance().reference.child("photos/profileImage")
        val localFile = File.createTempFile("tempImage", ".jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            image.setImageBitmap(bitmap)
        }

        petReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId)

        petReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value
                val score = snapshot.child("score").value
                val photo = snapshot.child("photo").value

                Glide.with(applicationContext)
                    .load(photo)
                    .placeholder(R.drawable.dog_placeholder)
                    .into(image)

                expReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nrOfElems = snapshot.childrenCount
                        Log.d("FRB", nrOfElems.toString())
                        val sum = updateXp(nrOfElems, 0, 0)
                        Log.d("SUM", sum.toString())
                        xpTV.text = (xpLong - sum).toString()

                        when {

                            score.toString().toInt() in 75..100 -> {
                                xpTV.setTextColor(resources.getColor(R.color.medium_green))
                            }
                            score.toString().toInt() in 31..74 -> {
                                xpTV.setTextColor(resources.getColor(R.color.yellow))
                            }
                            score.toString().toInt() in 0..30 -> {
                                xpTV.setTextColor(resources.getColor(R.color.red))
                            }
                        }

                        progressBar.progress = score.toString().toInt()
                        progressBar.max = 100

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                nameTV.text = name.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        expReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("expired")

        val options = FirebaseRecyclerOptions.Builder<ExpiredItem>()
            .setQuery(expReference, ExpiredItem::class.java)
            .setLifecycleOwner(this)
            .build()

        expList = arrayListOf()

        val rv = findViewById<RecyclerView>(R.id.expiredRV)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)

        expReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (elem in snapshot.children) {
                        val name = elem.child("name").value.toString()
                        val type = elem.child("type").value.toString()
                        val date = elem.child("date").value.toString()
                        val item = ExpiredItem(name, type, date)
                        expList.add(item)
                    }

                    rv.adapter = ExpiredAdapter(options)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        xpLong = Integer.parseInt(xpTV.text.toString()).toLong()

        val servicesRV = findViewById<RecyclerView>(R.id.servicesRV)
        val spacingDecorator = SpacingDecorator(50)
        list = ArrayList()
        list = populateRV()

        adapter = ServicesAdapter(list!!)
        servicesRV.adapter = adapter
        servicesRV.addItemDecoration(spacingDecorator)
        servicesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateXp(expired: Long, food: Long, extra: Long): Long {
        return expired * 40 + food * 20 + extra * 10
    }

    private fun populateRV(): ArrayList<ServiceItem> {

        val list: ArrayList<ServiceItem> = ArrayList()

        list.add(ServiceItem("Veterinary", R.drawable.vet))
        list.add(ServiceItem("Parks", R.drawable.park))
        list.add(ServiceItem("Pet Shops", R.drawable.pet_shop))
        return list
    }
}