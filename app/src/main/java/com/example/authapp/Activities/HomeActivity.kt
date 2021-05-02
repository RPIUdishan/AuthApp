package com.example.authapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import java.security.AccessController.getContext


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editTextUidInUserProfile.setText(auth.currentUser.uid)
        firestore.collection("users").document(auth.currentUser.uid).get()
            .addOnSuccessListener {
                var username = it["username"].toString()
                var email = it["email"].toString()
                var imageUri = it["profileImageUrl"].toString()
                editTextUsernameInUserProfile.setText(username)
                editTextEmailInUserProfile.setText(email)
                if (imageUri.isNotEmpty()) {
                    Glide.with(applicationContext).load(imageUri).into(imgViewUserProfile)
                }
            }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_profile -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.menu_logout -> signOutOperation()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOutOperation(){
        GoogleSignIn.getClient(
            applicationContext,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()

        LoginManager.getInstance().logOut();

        auth.signOut()
        var intent = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}