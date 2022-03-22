package com.example.mypet.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.login.Login
import com.example.mypet.model.Species

class RegisterFragment1 : Fragment(R.layout.fragment_register1) {

    lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToReg2Btn = view.findViewById<AppCompatButton>(R.id.nextRegButton)
        val petName = view.findViewById<EditText>(R.id.nameEditText)
        val petBreed = view.findViewById<EditText>(R.id.breedEditText)
        val sactv = view.findViewById<AutoCompleteTextView>(R.id.speciesAutoCompleteTextView)
        val loginTV = view.findViewById<TextView>(R.id.logInTextView)

        val speciesList = mutableListOf<String>()
        speciesList.add(Species.Bird.toString())
        speciesList.add(Species.Cat.toString())
        speciesList.add(Species.Dog.toString())
        speciesList.add(Species.Exotic.toString())
        speciesList.add(Species.Fish.toString())
        speciesList.add(Species.Rodent.toString())

        adapter = ArrayAdapter(requireContext(), R.layout.species_dropdown_item, speciesList)
        sactv.setAdapter(adapter)


        goToReg2Btn.setOnClickListener {
            val name = petName.text.toString().trim()
            val breed = petBreed.text.toString().trim()
            val species = sactv.text.toString()
            val action = RegisterFragment1Directions.actionRegisterFragment1ToRegisterFragment2(
                name,
                breed,
                species
            )
            view.findNavController().navigate(action)
        }

        loginTV.setOnClickListener {
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
        }
    }
}