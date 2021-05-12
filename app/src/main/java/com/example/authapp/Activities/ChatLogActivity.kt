@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.authapp.R
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authapp.Constants.Constants
import com.example.authapp.Models.MessageModel
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.receive_message.view.*
import kotlinx.android.synthetic.main.send_message.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatLogActivity : AppCompatActivity() {
    private val constant: Constants = Constants()
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var chatRoomID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = intent.getStringExtra(constant.CHAT_ROOM_NAME)
        chatRoomID  = intent.getStringExtra(constant.CHAT_ROOM_KEY)!!

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true
        recycler_gchat.layoutManager = layoutManager
        recycler_gchat.itemAnimator = DefaultItemAnimator()

        adapter = GroupAdapter()

        button_gchat_send.setOnClickListener {
            performMessaging()
        }

//        loadMessages()
        val docRef = firestore.collection("chatRooms").document(chatRoomID)
        docRef.addSnapshotListener(this) { value, error ->
            if (error != null){
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                Log.d("onstart", error.toString())
            }
            if(value?.exists()!!){
                loadMessages()
//                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onStart() {
        super.onStart()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_log_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.more_info_chat_room -> moreInfoIconFunctionality()
            R.id.menu_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.menu_logout -> signOutOperation()
        }
        return super.onOptionsItemSelected(item)

    }


    private fun moreInfoIconFunctionality(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val pw = PopupWindow(inflater.inflate(R.layout.popup_layout, null, false), 100, 100, true)
        pw.showAtLocation(findViewById(R.id.center_horizontal), Gravity.CENTER, 0, 0)
    }

    private fun performMessaging(){
        var message = edit_gchat_message.text.toString()
        var messageObj = MessageModel(message, auth?.currentUser.uid)

        firestore.collection("chatRooms")
            .document(chatRoomID.toString())
            .update(
                "messageList", FieldValue.arrayUnion(messageObj)
            )
            .addOnSuccessListener {
                Log.d("Message", "complete")
                adapter.add(SendItem(messageObj))
//                recycler_gchat.adapter = adapter
//                adapter.notifyDataSetChanged()
                edit_gchat_message.text.clear()
            }
            .addOnFailureListener {
                Log.d("Message F", "Failed")
            }
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

    private fun loadMessages(){

        val docRef = firestore.collection("chatRooms").document(chatRoomID)

//        docRef.addSnapshotListener(EventListener<DocumentSnapshot>(){
//                Eve
//        })
        firestore.collection("chatRooms").document(chatRoomID)
            .get()
            .addOnSuccessListener {
                var list = it["messageList"] as ArrayList<HashMap<*, MessageModel>>
                Log.d("i val", it["messageList"].toString())
                for(i in list) {
                    Log.d("test", "as")
                    Log.d("in", i.get("sender").toString())

                    if((i.get("sender").toString()).equals(auth.currentUser?.uid.toString())){
                        val messageObj = MessageModel( i.get("message").toString(), i.get("sender").toString(),
                            i.get("time").toString()
                        )

                        adapter.add(SendItem(messageObj))
                    }
                    else{
                        val messageObj = MessageModel( i.get("message").toString(), i.get("sender").toString(),
                            i.get("time").toString()
                        )
                        adapter.add(ReceiveItem(messageObj))
                    }

                }
                recycler_gchat.adapter = adapter
            }

        }

    inner class SendItem(var message: MessageModel): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val currentDate = sdf.format(Date())
            viewHolder.itemView.text_gchat_date_me.text = currentDate.toString()
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
            firestore.collection("users").document(message.sender)
            .get()
            .addOnSuccessListener {
                var username = it["username"].toString()
                viewHolder.itemView.text_gchat_user_other.text = username
                viewHolder.itemView.text_gchat_message_other.text = message.message
            }


        }

    }
}