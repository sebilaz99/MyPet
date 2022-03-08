package com.example.mypet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mypet.ui.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val homeFragment = HomeFragment()
    val extraFragment = ExtraFragment()
    val foodFragment = FoodFragment()
    val treatmentsFragment = TreatmentsFragment()
    val vaccinationsFragment = VaccinationsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(homeFragment)

        val bnv = findViewById<BottomNavigationView>(R.id.bottom_navigation)

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

}