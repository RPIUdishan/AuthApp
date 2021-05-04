@file:Suppress("PackageName")

package com.example.authapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authapp.Adapters.ChatRoomsAdapter
import com.example.authapp.Fragments.CreateChatRoomFragment
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.Models.MessageModel
import com.example.authapp.Models.UserModel
import com.example.authapp.R
import com.google.longrunning.ListOperationsRequest
import kotlinx.android.synthetic.main.activity_chat_room_list.*

private val chatRoomsList = ArrayList<ChatRoomModel>()
private lateinit var chatRoomAdapter: ChatRoomsAdapter
class ChatRoomListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room_list)
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
//
    private fun prepareData(){
        var userList = arrayListOf<UserModel>()
        var msgList = arrayListOf<MessageModel>()
        var chatRoom = ChatRoomModel("Test01", "Test01 Des", msgList, userList)
        chatRoomsList.add(chatRoom)
        chatRoom = ChatRoomModel("Test02", "Test02 Des", msgList, userList)
        chatRoomsList.add(chatRoom)
    }

    //create chat room method
    private fun createChatRoom(){

    }
}