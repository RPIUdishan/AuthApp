@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.example.authapp.Constants.TimeConstants
import com.example.authapp.R
import com.google.firebase.auth.FirebaseAuth

class SpalashActivity : AppCompatActivity() {

    private val timeConstantObj = TimeConstants()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)

        loadSplashScreen()
    }
    private fun loadSplashScreen(){
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,
                MainActivity::class.java)
            finish()
            startActivity(intent)
        }, timeConstantObj.SPLASH_TIME_OUT.toLong())
    }



    private fun actionBarHide(){
        val ab: ActionBar? = supportActionBar
        ab?.hide()
    }
}