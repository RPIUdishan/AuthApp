@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authapp.Adapters.ChatRoomsAdapter
import com.example.authapp.Fragments.CreateChatRoomFragment
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.Models.MessageModel
import com.example.authapp.Models.UserModel
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.longrunning.ListOperationsRequest
import kotlinx.android.synthetic.main.activity_chat_room_list.*

private val chatRoomsList = ArrayList<ChatRoomModel>()
private lateinit var chatRoomAdapter: ChatRoomsAdapter
private lateinit var auth: FirebaseAuth
private lateinit var firebase: FirebaseFirestore

class ChatRoomListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_list)

        supportActionBar?.title = "Chat Rooms"

        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()

        Log.d("ChatRoomActivity", "Created")
        chatRoomAdapter = ChatRoomsAdapter(chatRoomsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewUserChatRooms.layoutManager = layoutManager
        recyclerViewUserChatRooms.itemAnimator = DefaultItemAnimator()
        recyclerViewUserChatRooms.adapter = chatRoomAdapter
        prepareData()

        fabCreateChatRoom.setOnClickListener {
            val fragment = supportFragmentManager
            val createChatRoomFragment = CreateChatRoomFragment()
            createChatRoomFragment.show(fragment, "Create Chat Room")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.join_with_chat_room -> startActivity(Intent(this, AllChatRoomsActivity::class.java))
            R.id.menu_profile -> startActivity(Intent(this, SignUpActivity::class.java))
            R.id.menu_logout -> signOutOperation()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepareData(){
        var userList = arrayListOf<String>()
        var msgList = arrayListOf<MessageModel>()
        var chatRoom = ChatRoomModel("Test01", "Test01 Des", msgList, userList)
        chatRoomsList.add(chatRoom)
        chatRoom = ChatRoomModel("Test02", "Test02 Des", msgList, userList)
        chatRoomsList.add(chatRoom)
    }

//    private fun prepareData2(){
//        firebase.collection("users")
//                .document(auth.currentUser.uid)
//                .get()
//                .addOnSuccessListener {
//                    it["userChatRoomModelList"] as
//                }
//    }

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