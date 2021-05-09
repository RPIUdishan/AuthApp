@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authapp.Adapters.ChatRoomsAdapter
import com.example.authapp.Fragments.CreateChatRoomFragment
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.Models.MessageModel
import com.example.authapp.R
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_room_list.*
import kotlinx.android.synthetic.main.chat_room_item.view.*


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

//        userChatRooms()

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




//    private fun userChatRooms(){
//        firebase.collection("users")
//            .document(auth?.currentUser.uid)
//            .get()
//            .addOnSuccessListener { doc1 ->
//                var x = arrayOf(doc1["userChatRoomModelList"])
//                var tempArray: ArrayList<String> = ArrayList()
//                x.forEach{ doc2 ->
//                    Log.d("elem", doc2.toString())
//                    tempArray.add(x.toString())
//                }
//
//
//                tempArray.forEach { doc3 ->
//                    Log.d("myArray111", doc3)
//                    firebase.collection("chatRooms")
//                        .whereArrayContains("userModelList", doc3)
//                        .get()
//                        .addOnSuccessListener { doc4 ->
//                            for(doc in doc4){
//                                Log.d("ItemElem", doc4.documents.toString())
//                                val chatRoom = doc.toObject<ChatRoomModel>()
//                                adapter.add(ChatRoomItem(chatRoom))
//                            }
//
//                            recyclerViewUserChatRooms.adapter = adapter
//                        }
//                }
//
//            }
//    }
    private fun signOutOperation(){
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

//    inner class ChatRoomItem(private val chatRoom: ChatRoomModel): Item<GroupieViewHolder>() {
//
//        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//            viewHolder.itemView.textViewChatRoomName.text = chatRoom.chatRoomName
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.chat_room_item
//        }
//    }
}
