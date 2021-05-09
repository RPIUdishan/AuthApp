@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.authapp.Constants.Constants
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import kotlinx.android.synthetic.main.activity_all_chat_rooms.*
import kotlinx.android.synthetic.main.chat_room_item.view.*

class AllChatRoomsActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private val constant: Constants = Constants()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chat_rooms)

        adapter = GroupAdapter()
        supportActionBar?.title = "All Chat Rooms"

        adapter.setOnItemClickListener { item, view ->

            val chatRoom = item as ChatRoomItem

            var intent = Intent(applicationContext, ChatLogActivity::class.java)
            Log.d("ItemID", chatRoom.chatRoomId)
            intent.putExtra(constant.CHAT_ROOM_KEY,chatRoom.chatRoomId)
            intent.putExtra(constant.CHAT_ROOM_NAME, chatRoom.chatRoomName)
            startActivity(intent)
        }
        getAllChatRooms()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_rooms_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.menu_logout -> signOutOperation()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAllChatRooms() {
        val ref = FirebaseFirestore.getInstance().collection("chatRooms")
        ref.addSnapshotListener { value, error ->

            error?.let {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG)
                    .show()
                return@addSnapshotListener
            }

            value?.let {
                for (document in it) {
                    var chatRoomId = document.id
                    var chatRoomName = document["chatRoomName"].toString()
                    var chatRoomPic = document["chatRoomPic"].toString()
                    adapter.add(ChatRoomItem(chatRoomId, chatRoomName, chatRoomPic))
                }

                recycleViewAllChatRooms.adapter = adapter
            }
        }
    }

    private fun signOutOperation() {
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

    inner class ChatRoomItem(
         var chatRoomId: String,
         var chatRoomName: String,
         var chatRoomPic: String
    ) : Item<GroupieViewHolder>() {

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

