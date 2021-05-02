@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.example.authapp.Constants.Constants
import com.example.authapp.R

class SpalashActivity : AppCompatActivity() {

    private val timeConstantObj = Constants()
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

}