@file:Suppress("PackageName")

package com.example.authapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.authapp.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }
}