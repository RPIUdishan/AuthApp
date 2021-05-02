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
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {
    private val utils: CommonUtils = CommonUtils()
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //initialised firebase auth
        auth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            if (utils.isNetworkAvailable(applicationContext)) {
                if (validationCheckInEmailPwdAuth()) {
                    Log.d("Sign Up - emailpwd", "Ok")
                    createAccount(editTextEmail.text.toString(), editTextPassword.text.toString())
                    Log.d("createAccount", auth.currentUser.uid)
                    startActivity(Intent(applicationContext, SignInActivity::class.java))
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

    private fun createAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createAccount", "createUserWithEmail:success")
                    val currentUser = auth.currentUser
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w("createAccount", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    //firebase authentication
    private fun firebaseAuthentication(){

    }
}