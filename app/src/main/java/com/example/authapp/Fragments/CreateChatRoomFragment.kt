@file:Suppress("PackageName")

package com.example.authapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.authapp.Activities.ProfileActivity
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.Models.MessageModel
import com.example.authapp.Models.UserModel
import com.example.authapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dialog_create_chat_room.view.*

class CreateChatRoomFragment: DialogFragment() {
    lateinit var firebase: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var viewRoot: View = inflater.inflate(R.layout.fragment_dialog_create_chat_room, container, false)

        var cancelButton = viewRoot.btnChatRoomCreateCancel
        var saveButton = viewRoot.btnChatRoomCreateCreate

        cancelButton.setOnClickListener {
            onDestroyView()
        }

        saveButton.setOnClickListener {
            Log.d("save button", " clicked")

            var chatRoomName: String = viewRoot.editTextTextChatRoomName.text.toString()
            var chatRoomDescription: String = viewRoot.editTextTextChatRoomDescription.text.toString()
            var msgList: ArrayList<MessageModel> = arrayListOf()
            var userList: ArrayList<String> = ArrayList()
            userList.add(auth.currentUser.uid)
            chatRoomInsertToFirebase(chatRoomName, chatRoomDescription, msgList, userList)
        }
    return viewRoot
    }

    private fun chatRoomInsertToFirebase(chatRoomName: String, chatRoomDescription: String, msgList: ArrayList<MessageModel>, userList: ArrayList<String>){
        var chatRoomModelObj: ChatRoomModel = ChatRoomModel(chatRoomName, chatRoomDescription, msgList, userList)
        firebase.collection("chatRooms")
                .document(chatRoomName)
                .set(chatRoomModelObj)
                .addOnSuccessListener {
                    Log.d("ChatRoomCreated", "$chatRoomName is inserted")
                    Toast.makeText(this.context, "$chatRoomName is Created", Toast.LENGTH_LONG)
                            .show()
                    val intent = Intent(this.context, ProfileActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("ChatRoomCreated", "$it is raised")
                    Toast.makeText(this.context, "$chatRoomName is Created", Toast.LENGTH_LONG)
                            .show()
                }
    }
}