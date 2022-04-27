package com.example.mypet

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
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
import com.example.mypet.adapter.AppointmentsAdapter
import com.example.mypet.adapter.ExpiredAdapter
import com.example.mypet.adapter.ServicesAdapter
import com.example.mypet.adapter.SpacingDecorator
import com.example.mypet.login.Login
import com.example.mypet.model.AppointmentItem
import com.example.mypet.model.ExpiredItem
import com.example.mypet.model.ServiceItem
import com.example.mypet.model.UserStatus
import com.example.mypet.ui.activities.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var petReference: DatabaseReference
    private lateinit var expReference: DatabaseReference
    private lateinit var appReference: DatabaseReference
    private lateinit var foodReference: DatabaseReference
    private var ownerId = ""
    private lateinit var expList: ArrayList<ExpiredItem>
    var xpLong by Delegates.notNull<Long>()
    private lateinit var adapter: ServicesAdapter
    private lateinit var appAdapter: AppointmentsAdapter
    private var list: ArrayList<ServiceItem>? = null
    private var appList: ArrayList<AppointmentItem>? = null
    private lateinit var storageRef: StorageReference
    private var dateList: ArrayList<String>? = null
    private lateinit var referencesList: ArrayList<Pair<String, Long>>
    private val taskMap: MutableMap<String, Any> = HashMap()

    private lateinit var toggle: ActionBarDrawerToggle

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
        val bnv = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)

        val header = nv.getHeaderView(0)

        val xpTV = header.findViewById<TextView>(R.id.xpTextView)
        val nameTV = header.findViewById<TextView>(R.id.petNameTextView)
        val image = header.findViewById<CircleImageView>(R.id.circleImageView)
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val upcomingTV = findViewById<TextView>(R.id.upcomingTV)
        val nrOfDaysTV = findViewById<TextView>(R.id.daysTV)
        val typeTV = findViewById<TextView>(R.id.typeTV)
        val upcomingConstraint = findViewById<ConstraintLayout>(R.id.constraintLayout2)
        val autoUpdateBtn = findViewById<Button>(R.id.autoUpdateScoreButton)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val progressBar = header.findViewById<ProgressBar>(R.id.progressBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("food")

        petReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId)

        expReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("expired")

        foodReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("food")

        appReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("appointments")

        autoUpdateBtn.setOnClickListener {
            referencesList = arrayListOf()
            expReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val counter = snapshot.childrenCount
                    referencesList.add(Pair("expired", counter))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            appReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val counter = snapshot.childrenCount
                    referencesList.add(Pair("appointments", counter))
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            foodReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val counter = snapshot.childrenCount
                    referencesList.add(Pair("food", counter))
                    Log.d("TEST", referencesList[1].second.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            petReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scoreSnapshot = snapshot.child("score").value
                    var scoreLong = scoreSnapshot.toString().toLong()
                    scoreLong =
                        scoreLong - (40 * referencesList[0].second) + (10 * referencesList[1].second) + (20 * referencesList[2].second)
                    xpTV.text = scoreLong.toString()

                    when {
                        scoreLong.toInt() in 75..100 ->
                            xpTV.setTextColor(resources.getColor(R.color.medium_green))

                        scoreLong.toInt() in 31..74 ->
                            xpTV.setTextColor(resources.getColor(R.color.yellow))

                        scoreLong.toInt() in 0..30 ->
                            xpTV.setTextColor(resources.getColor(R.color.red))
                    }

                    progressBar.progress = scoreLong.toInt()
                    progressBar.max = 100
                    progressBar.min = 0
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        autoUpdateBtn.performClick()


        nv.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.profileItem -> {
                    startActivity(Intent(this, Profile::class.java))
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
                }
            }
            true
        }

        bnv.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.vaccination -> {
                    startActivity(Intent(this, Vaccination::class.java))
                }
                R.id.medication -> {
                    startActivity(Intent(this, Medication::class.java))
                }
                R.id.food -> {
                    startActivity(Intent(this, Food::class.java))
                }
            }
            true
        }

        val currentDate = LocalDate.now().toString()
        val currentYear = currentDate.subSequence(0, 4).toString()
        val currentMonth = currentDate.subSequence(5, 7).toString()
        val currentDay = currentDate.subSequence(8, 10).toString()
        val currentDateStringFormat = "$currentDay-$currentMonth-$currentYear"
        val currentDateNewFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale.UK).parse(currentDateStringFormat)


        appReference.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dateList = ArrayList()
                    val upcomingValSnap = snapshot.childrenCount
                    val txt1 = findViewById<TextView>(R.id.textView8)
                    val txt2 = findViewById<TextView>(R.id.textView9)

                    if (snapshot.exists()) {
                        for (elem in snapshot.children) {
                            val date = elem.child("date").value.toString()
                            dateList!!.add(date)
                        }

                        val sortedDateList = dateList!!.sortedBy {
                            LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        }

                        val timestampDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.UK).parse(sortedDateList[0])
                        val diffInMillis: Long = timestampDate.time - currentDateNewFormat.time
                        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                        if (diffInDays.toInt() == 0) {
                            txt1.text = "today"
                            nrOfDaysTV.text = ""
                            txt2.text = ""
                        } else if (diffInDays.toInt() < 0) {
                            txt1.text = "in"
                            nrOfDaysTV.text = "the"
                            txt2.text = "past"
                        } else {
                            nrOfDaysTV.text = diffInDays.toString()
                        }
                    } else {
                        typeTV.text = "no"
                        nrOfDaysTV.text = ""
                        txt1.text = "appointments"
                        txt2.text = ""
                    }
                    upcomingTV.text = upcomingValSnap.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        storageRef = FirebaseStorage.getInstance().reference.child("photos/$ownerId")
        val localFile = File.createTempFile("tempImage", ".jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            image.setImageBitmap(bitmap)
        }

        petReference.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value
                    val photo = snapshot.child("photo").value

                    Glide.with(applicationContext)
                        .load(photo)
                        .placeholder(R.drawable.dog_placeholder)
                        .into(image)

                    nameTV.text = name.toString()
                    nameTextView.text = name.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        val options = FirebaseRecyclerOptions.Builder<ExpiredItem>()
            .setQuery(expReference, ExpiredItem::class.java)
            .setLifecycleOwner(this)
            .build()

        expList = arrayListOf()

        val spacingDecorator2 = SpacingDecorator(0, 10)
        val rv = findViewById<RecyclerView>(R.id.expiredRV)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(spacingDecorator2)
        rv.setHasFixedSize(true)

        expReference.addValueEventListener(
            object : ValueEventListener {
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
        val appointmentsRV = findViewById<RecyclerView>(R.id.appointmentsRV)
        val spacingDecorator = SpacingDecorator(50, 0)

        list = ArrayList()
        list = populateRV()

        appList = ArrayList()
        appList = populateAppRV()

        adapter = ServicesAdapter(list!!)
        servicesRV.adapter = adapter
        servicesRV.addItemDecoration(spacingDecorator)
        servicesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        appAdapter = AppointmentsAdapter(appList!!)
        appointmentsRV.adapter = appAdapter
        appointmentsRV.addItemDecoration(spacingDecorator)
        appointmentsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        upcomingConstraint.setOnClickListener {
            val intent = Intent(this, Appointment::class.java)
            intent.putExtra("type", " ")
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateRV(): ArrayList<ServiceItem> {
        val list: ArrayList<ServiceItem> = ArrayList()
        list.add(ServiceItem("Veterinary", R.drawable.vet))
        list.add(ServiceItem("Parks", R.drawable.park))
        list.add(ServiceItem("Pet Shops", R.drawable.petshop))
        return list
    }

    private fun populateAppRV(): ArrayList<AppointmentItem> {
        val list: ArrayList<AppointmentItem> = ArrayList()
        list.add(AppointmentItem("Veterinary", R.drawable.veterinary))
        list.add(AppointmentItem("Grooming", R.drawable.grooming))
        return list
    }
}