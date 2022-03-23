package com.example.mypet

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.mypet.login.Login
import com.example.mypet.model.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var ownerId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        ownerId = auth.currentUser!!.uid

        reference = FirebaseDatabase.getInstance().reference.child("Pet")
            .child(ownerId).child("owner")

        supportActionBar?.hide()

        val animation = findViewById<LottieAnimationView>(R.id.animationView)


        animation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                reference.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val status = dataSnapshot.child("status").value
                        when (status) {
                            UserStatus.ONLINE.toString() -> startActivity(
                                Intent(
                                    applicationContext,
                                    MainActivity::class.java
                                )
                            )
                            UserStatus.OFFLINE.toString() -> startActivity(
                                Intent(
                                    applicationContext,
                                    Login::class.java
                                )
                            )
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("snapshot: ", databaseError.code.toString())
                    }
                })
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
    }
}