@file:Suppress("PackageName")

package com.example.authapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.authapp.CommonUtils.CommonUtils
import com.example.authapp.R
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    private val utils: CommonUtils = CommonUtils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignUp.setOnClickListener {
            if (utils.isNetworkAvailable(applicationContext)) {
                if (validationCheckInEmailPwdAuth()) {
                    Log.d("Sign Up - emailpwd", "Ok")
                }
            } else {
                val snack = Snackbar.make(it, "No Internet Connect", Snackbar.LENGTH_LONG)
                snack.show()
            }
        }
    }

    //field validation for email password authentication
    private fun validationCheckInEmailPwdAuth(): Boolean {

        when {
            TextUtils.isEmpty(editTextEmail.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Email",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) -> {
                Toast.makeText(
                    this,
                    "Invalid email",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            TextUtils.isEmpty(editTextPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Password",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            TextUtils.isEmpty(
                editTextReEnterPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please ReEnter Your Password",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            else -> {
                var pwd = editTextPassword.text.toString()
                var repwd = editTextReEnterPassword.text.toString()
                if (pwd == repwd) {
                    return true
                } else {
                    Toast.makeText(
                        this,
                        "Password does not matched.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

            }
        }

        return false
    }
}