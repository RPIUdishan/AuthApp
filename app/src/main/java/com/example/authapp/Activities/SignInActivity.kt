@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.authapp.CommonUtils.CommonUtils
import com.example.authapp.Constants.Constants
import com.example.authapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.editTextPassword
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val utils: CommonUtils = CommonUtils()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val constant: Constants = Constants()
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogleSignIn.setOnClickListener {
            if (utils.isNetworkAvailable(applicationContext)) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, constant.RC_SIGN_IN)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Google Sign in", "$requestCode")
        Log.d("Google Sign in", "${requestCode == constant.RC_SIGN_IN}")
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == constant.RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("Google Sign indata", "$data")
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Google Sign in", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Sign in", "$e")
            }
        }
//        else{ callbackManager.onActivityResult(requestCode, resultCode, data) }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebaseAuthWithGoogle", "signInWithCredential:success")
                    finish()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("firebaseAuthWithGoogle", "signInWithCredential:failure", task.exception)

                }
            }
    }
}