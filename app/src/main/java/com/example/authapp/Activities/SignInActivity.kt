@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.authapp.CommonUtils.CommonUtils
import com.example.authapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.editTextPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val utils: CommonUtils = CommonUtils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //initialised firebase auth
        auth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener {
            if (utils.isNetworkAvailable(applicationContext)) {
                if (validationCheckInEmailPwdAuth()) {
                    Log.d("Sign In - emailpwd", "Ok")
                    signInWithEmailPassword(editTextEmailSignIn.text.toString(), editTextPassword.text.toString())
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

            TextUtils.isEmpty(editTextEmailSignIn.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Email",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmailSignIn.text.toString()).matches()) -> {
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

            else -> {
                    return true
                }
            }
        return false
    }

    private fun signInWithEmailPassword(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("signInWithEmailPassword", "signInWithEmail:success")
                    val user = auth.currentUser
                    finish()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signInWithEmailPassword", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
        // [END sign_in_with_email]
    }
}