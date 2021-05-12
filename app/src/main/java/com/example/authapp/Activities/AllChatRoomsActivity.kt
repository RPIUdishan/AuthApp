@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.app.AlertDialog
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
import com.google.firebase.firestore.FieldValue
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
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_chat_rooms)

        adapter = GroupAdapter()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        supportActionBar?.title = "All Chat Rooms"


        adapter.setOnItemClickListener { item, view ->
            val chatRoom = item as ChatRoomItem
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you need to enter this chat room ?")
            builder.setMessage(chatRoom.chatRoomName)

            builder.setPositiveButton("Yes") { dialogInterface, which ->


                firestore.collection("users")
                    .document(auth.currentUser.uid)
                    .update(
                        "userChatRoomModelList",
                        FieldValue.arrayUnion(chatRoom.chatRoomId)
                    )

                    .addOnSuccessListener {

                        firestore.collection("chatRooms")
                            .document(chatRoom.chatRoomId)
                            .update(
                                "userModelList",
                                FieldValue.arrayUnion(auth.currentUser?.uid)
                            )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Successfully add your Chat Room",
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()
                                var intent = Intent(
                                    applicationContext,
                                    ChatLogActivity::class.java
                                )
                                intent.putExtra(
                                    constant.CHAT_ROOM_KEY,
                                    chatRoom.chatRoomId
                                )
                                intent.putExtra(
                                    constant.CHAT_ROOM_NAME,
                                    chatRoom.chatRoomName
                                )

                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to Complete Chat Room Creation",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                var intent = Intent(
                                    applicationContext,
                                    CreateChatRoomActivity::class.java
                                )
                                startActivity(intent)
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed to Complete Chat Room Creation",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        var intent = Intent(
                            applicationContext,
                            CreateChatRoomActivity::class.java
                        )
                        startActivity(intent)
                    }

                intent.putExtra(constant.CHAT_ROOM_KEY, chatRoom.chatRoomId)
                intent.putExtra(constant.CHAT_ROOM_NAME, chatRoom.chatRoomName)
                startActivity(intent)
            }

            //performing cancel action
            builder.setNeutralButton("Cancel") { dialogInterface, which ->
                onBackPressed()
            }
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
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

                firestore.collection("users")
                    .document(auth?.currentUser.uid)
                    .get()
                    .addOnSuccessListener {
                        var list = it["userChatRoomModelList"] as ArrayList<String>
                        if (!list.contains(chatRoomId)) {
                            adapter.add(ChatRoomItem(chatRoomId, chatRoomName, chatRoomPic))
                        }
                    }
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

