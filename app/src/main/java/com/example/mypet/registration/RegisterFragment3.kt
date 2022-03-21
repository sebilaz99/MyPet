package com.example.mypet.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mypet.MainActivity
import com.example.mypet.R


class RegisterFragment3 : Fragment(R.layout.fragment_register3) {

    val args: RegisterFragment3Args by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToReg2Btn = view.findViewById<AppCompatButton>(R.id.backToRegister2Button)
        val finishRegBtn = view.findViewById<AppCompatButton>(R.id.finishRegistrationButton)
        val emailET = view.findViewById<EditText>(R.id.emailEditText)
        val passwordET = view.findViewById<EditText>(R.id.passwordEditText)

        val name = args.name
        val breed = args.breed
        val species = args.species
        val sex = args.sex
        val color = args.colour
        val dob = args.dob
        val email = emailET.text.toString()
        val password = passwordET.text.toString()


        goToReg2Btn.setOnClickListener {
            val action =
                RegisterFragment3Directions.actionRegisterFragment3ToRegisterFragment2("", "", "")
            view.findNavController().navigate(action)
        }

        finishRegBtn.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }

    }
}