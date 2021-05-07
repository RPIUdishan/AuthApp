@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_log_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.more_info_chat_room -> Log.d("asd", "asd")
            R.id.menu_profile -> startActivity(Intent(this, ProfileActivity::class.java))
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
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signOut()
        var intent = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}