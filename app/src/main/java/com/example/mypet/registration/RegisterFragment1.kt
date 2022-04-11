package com.example.mypet.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.mypet.R
import com.example.mypet.login.Login

class RegisterFragment1 : Fragment(R.layout.fragment_register1) {

    lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToReg2Btn = view.findViewById<AppCompatButton>(R.id.nextRegButton)
        val petName = view.findViewById<EditText>(R.id.nameEditText)
        val petBreed = view.findViewById<EditText>(R.id.breedEditText)
        val loginTV = view.findViewById<TextView>(R.id.logInTextView)


        goToReg2Btn.setOnClickListener {
            val name = petName.text.toString().trim()
            val breed = petBreed.text.toString().trim()
            val action = RegisterFragment1Directions.actionRegisterFragment1ToRegisterFragment2(
                name,
                breed
            )
            view.findNavController().navigate(action)
        }

        loginTV.setOnClickListener {
            val intent = Intent(context, Login::class.java)
            startActivity(intent)
        }
    }
}