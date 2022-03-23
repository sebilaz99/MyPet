package com.example.mypet.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.Owner
import com.example.mypet.model.Pet
import com.example.mypet.model.Sex
import com.example.mypet.model.UserStatus
import com.example.mypet.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterFragment3 : Fragment(R.layout.fragment_register3) {

    val args: RegisterFragment3Args by navArgs()

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private var ownerId = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToReg2Btn = view.findViewById<AppCompatButton>(R.id.backToRegister2Button)
        val finishRegBtn = view.findViewById<AppCompatButton>(R.id.finishRegistrationButton)
        val emailET = view.findViewById<EditText>(R.id.emailEditText)
        val passwordET = view.findViewById<EditText>(R.id.passwordEditText)
        val passwordET2 = view.findViewById<EditText>(R.id.passwordEditText2)

        val name = args.name
        val breed = args.breed
        val species = args.species
        val sex = args.sex
        val color = args.colour
        val dob = args.dob


        auth = FirebaseAuth.getInstance()

        goToReg2Btn.setOnClickListener {
            val action =
                RegisterFragment3Directions.actionRegisterFragment3ToRegisterFragment2("", "", "")
            view.findNavController().navigate(action)
        }

        finishRegBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()
            val password2 = passwordET2.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(context, "Please type your email", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(context, "Please type your password", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password2)) {
                Toast.makeText(context, "Please re-type your password", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(
                    context,
                    "Password must contains at least 8 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != password2) {
                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(
                    password
                ) && !TextUtils.isEmpty(password2) && password.length >= 8 && password == password2
            ) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ownerId = auth.currentUser!!.uid
                            reference = FirebaseDatabase.getInstance().reference.child("Pet")
                                .child(ownerId)

                            val owner = Owner(email, password, UserStatus.ONLINE.toString())
                            val pet = Pet(
                                ownerId,
                                name,
                                sex,
                                dob,
                                species,
                                breed,
                                color,
                                "",
                                Constants.intialScore,
                                owner
                            )
                            reference.setValue(pet)

                            val intent = Intent(activity, MainActivity::class.java)
                            activity?.startActivity(intent)
                        } else {
                            Toast.makeText(
                                context,
                                "Error while creating your account",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }


        }

    }
}