package com.example.mypet.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.mypet.MainActivity
import com.example.mypet.R
import com.example.mypet.model.UserStatus
import com.example.mypet.registration.Register
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var ownerId: String
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()


        val logBtn = findViewById<AppCompatButton>(R.id.logInButton)
        val email = findViewById<EditText>(R.id.emailET)
        val password = findViewById<EditText>(R.id.passwordET)
        val regTV = findViewById<TextView>(R.id.registerTextView)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        ownerId = user.uid

        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("owner")

        logBtn.setOnClickListener {
            val emailStr = email.text.toString().trim()
            val passwordStr = password.text.toString().trim()

            if (TextUtils.isEmpty(emailStr)) {
                Toast.makeText(this, "Please type your email", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(passwordStr)) {
                Toast.makeText(this, "Please type your password", Toast.LENGTH_SHORT).show()
            } else if (!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr)) {
                auth.signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val taskMap: MutableMap<String, Any> = HashMap()
                            taskMap["status"] = UserStatus.ONLINE.toString()
                            reference.updateChildren(taskMap)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Your credentials are incorrect!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
            }
        }

        regTV.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}