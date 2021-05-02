@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.authapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialised firebase auth
        auth = FirebaseAuth.getInstance()

        //sign in btn action
        btn_main_screen_sign_in.setOnClickListener {
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }

        //sign up btn action
        btn_main_screen_sign_up.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
    }

    //on start method
    override fun onStart() {
        super.onStart()
        //check user user loggd or not
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d("Logged", currentUser.uid)
            var intent = Intent(applicationContext, HomeActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}