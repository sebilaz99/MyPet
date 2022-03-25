package com.example.mypet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mypet.login.Login
import com.example.mypet.model.UserStatus
import com.example.mypet.ui.activities.Profile
import com.example.mypet.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    val homeFragment = HomeFragment()
    val extraFragment = ExtraFragment()
    val foodFragment = FoodFragment()
    val treatmentsFragment = TreatmentsFragment()
    val vaccinationsFragment = VaccinationsFragment()

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var petReference: DatabaseReference
    private var ownerId = ""

    private val progress = 0

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

        val bnv = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val nv = findViewById<NavigationView>(R.id.navigationView)

        val header = nv.getHeaderView(0)

        val xpTV = header.findViewById<TextView>(R.id.xpTextView)
        val nameTV = header.findViewById<TextView>(R.id.petNameTextView)

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
                R.id.settingsItem -> Log.d("Nav", "Settings")
                R.id.signOutItem -> {
                    val taskMap: MutableMap<String, Any> = HashMap()
                    taskMap["status"] = UserStatus.OFFLINE.toString()
                    reference.updateChildren(taskMap)
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        petReference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId)

        petReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value
                val score = snapshot.child("score").value
                val photo = snapshot.child("photo").value

                xpTV.text = score.toString()
                nameTV.text = name.toString()

                if (score.toString().toInt() in 75..100) {
                    xpTV.setTextColor(resources.getColor(R.color.medium_green))
                } else if (score.toString().toInt() in 31..74) {
                    xpTV.setTextColor(resources.getColor(R.color.yellow))
                } else if (score.toString().toInt() in 0..30) {
                    xpTV.setTextColor(resources.getColor(R.color.red))
                }

                progressBar.progress = score.toString().toInt()
                progressBar.max = 100

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        bnv.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.extra ->
                    replaceFragment(extraFragment)
                R.id.home ->
                    replaceFragment(homeFragment)
                R.id.food -> replaceFragment(foodFragment)
                R.id.treatments -> replaceFragment(treatmentsFragment)
                R.id.vaccinations -> replaceFragment(vaccinationsFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}