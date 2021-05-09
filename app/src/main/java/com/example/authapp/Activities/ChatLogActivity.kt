@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.authapp.Constants.Constants
import com.example.authapp.Models.MessageModel
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_room_list.*
import kotlinx.android.synthetic.main.receive_message.view.*
import kotlinx.android.synthetic.main.send_message.view.*

class ChatLogActivity : AppCompatActivity() {
    private val constant: Constants = Constants()
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = intent.getStringExtra(constant.CHAT_ROOM_NAME)
        var chatRoomID: String? = intent.getStringExtra(constant.CHAT_ROOM_KEY)

        val layoutManager = LinearLayoutManager(applicationContext)
        recycler_gchat.layoutManager = layoutManager
        recycler_gchat.itemAnimator = DefaultItemAnimator()

        adapter = GroupAdapter()

//        addData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_log_menu, menu)
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

//    private fun addData(){
//        adapter.add(SendItem(MessageModel("111", "Madhu", "Ishanka")))
//        adapter.add(ReceiveItem(MessageModel("112", "Ishanka", "Madhu")))
//
//        recycler_gchat.adapter = adapter
//    }

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

    inner class SendItem(var message: MessageModel): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.text_gchat_message_me.text = message.message
        }

        override fun getLayout(): Int {
            return R.layout.send_message
        }
    }

    inner class ReceiveItem(var message: MessageModel): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.receive_message
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.text_gchat_message_other.text = message.message
        }

    }
}