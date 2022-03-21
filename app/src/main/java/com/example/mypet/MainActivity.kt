package com.example.mypet

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mypet.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    val homeFragment = HomeFragment()
    val extraFragment = ExtraFragment()
    val foodFragment = FoodFragment()
    val treatmentsFragment = TreatmentsFragment()
    val vaccinationsFragment = VaccinationsFragment()

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(homeFragment)

        val bnv = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val nv = findViewById<NavigationView>(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nv.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profileItem -> Log.d("Nav", "Profile")
                R.id.settingsItem -> Log.d("Nav", "Settings")
                R.id.signOutItem -> Log.d("Nav", "SignOut")
            }
            true
        }

        bnv.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.extra ->
                    replaceFragment(extraFragment)
                R.id.home -> replaceFragment(homeFragment)
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