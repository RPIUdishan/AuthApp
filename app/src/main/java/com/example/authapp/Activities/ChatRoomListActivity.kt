@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.authapp.Adapters.ChatRoomsAdapter
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_all_chat_rooms.*
import kotlinx.android.synthetic.main.activity_chat_room_list.*
import kotlinx.android.synthetic.main.chat_room_item.view.*

private val chatRoomsList = ArrayList<ChatRoomModel>()
private lateinit var auth: FirebaseAuth
private lateinit var firebase: FirebaseFirestore
lateinit var adapter: GroupAdapter<GroupieViewHolder>

class ChatRoomListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_list)

        supportActionBar?.title = "Chat Rooms"

        adapter = GroupAdapter()

        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()

        Log.d("ChatRoomActivity", "Created")
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewUserChatRooms.layoutManager = layoutManager
        recyclerViewUserChatRooms.itemAnimator = DefaultItemAnimator()
        getUserChatRooms()


        fabCreateChatRoom.setOnClickListener {
            var intent = Intent(applicationContext, CreateChatRoomActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.join_with_chat_room -> startActivity(
                Intent(
                    this,
                    AllChatRoomsActivity::class.java
                )
            )
            R.id.menu_profile -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.menu_logout -> signOutOperation()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOutOperation() {
        GoogleSignIn.getClient(
            applicationContext,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()

        LoginManager.getInstance().logOut()

        auth.signOut()
        var intent = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun getUserChatRooms() {

        val refUser: CollectionReference = FirebaseFirestore.getInstance().collection("users")

        refUser.document(auth.currentUser.uid)
            .get()
            .addOnSuccessListener {
                var chatRoomList = it["userChatRoomModelList"] as ArrayList<String>
                for (i in chatRoomList) {
                    Log.d("item", i)
                    firebase.collection("chatRooms").document(i)
                        .get()
                        .addOnSuccessListener {
                            var chatRoomName = it["chatRoomName"].toString()
                            var chatRoomPic = it["chatRoomPic"].toString()

                            adapter.add(ChatRoomItem(chatRoomName, chatRoomPic))
                        }
                }
                recyclerViewUserChatRooms.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Cannot get List", Toast.LENGTH_LONG)
                    .show()
            }
    }

    inner class ChatRoomItem(private var chatRoomName: String, private var chatRoomPic: String) :
        Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textViewChatRoomName.text = chatRoomName
            if (chatRoomPic.isNotEmpty()) {
                Glide.with(applicationContext).load(chatRoomPic)
                    .into(viewHolder.itemView.imageViewChatRoom)
            }
        }

        override fun getLayout(): Int {
            return R.layout.chat_room_item
        }
    }
}
