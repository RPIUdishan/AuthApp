@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.app.Activity
import android.content.Intent
import android.content.Intent.createChooser
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.authapp.Constants.Constants
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_chat_room.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class CreateChatRoomActivity : AppCompatActivity() {
    private val constant: Constants = Constants()
    private var selectedPhotoUri: Uri? = null
    lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_chat_room)

        //initialised firestore instance
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        textViewChatRoomImageUpload.setOnClickListener {
            startFileChooser()
        }

        btnChatRoomCreateCreate.setOnClickListener {
            var chatRoomName = editTextTextChatRoomName.text.toString()
            var chatRoomDescription = editTextTextChatRoomDescription.text.toString()
            saveChatRoom(chatRoomName, chatRoomDescription, selectedPhotoUri.toString())
        }
    }

    private fun startFileChooser() {
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(createChooser(intent, "Choose a Picture"), 111)
    }

    private fun setChatRoomPicUrl(selectedPhotoUri: Uri) {
        this.selectedPhotoUri = selectedPhotoUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == constant.RC_IMAGE_URI && resultCode == Activity.RESULT_OK && data != null) {

            var tempUri = data.data!!
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/chatRoomPics/$filename")

            ref.putFile(tempUri!!)
                .addOnSuccessListener {
                    Log.d(
                        "SetingsProfileActivity",
                        "Successfully uploaded image: ${it.metadata?.path}"
                    )

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("SettingProfile", "FileLocation $it")
                        setChatRoomPicUrl(it)
                    }
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, tempUri)
                    imgViewChatRoomUploader.setImageBitmap(bitmap)
                }
        }
    }

    private fun saveChatRoom(
        chatRoomName: String,
        chatRoomDescription: String,
        selectedPhotoUri: String) {

        val chatRoomID = UUID.randomUUID().toString()
        val chatRoomModel = ChatRoomModel(chatRoomName, chatRoomDescription, selectedPhotoUri)
        chatRoomModel.addUsersToUserList(auth.currentUser.uid)

        firestore.collection("chatRooms")
            .document(chatRoomID)
            .set(chatRoomModel)
            .addOnSuccessListener {

                firestore.collection("users")
                    .document(auth.currentUser.uid)
                    .update(
                        "userChatRoomModelList",
                        FieldValue.arrayUnion(chatRoomID))

                    .addOnSuccessListener {
                        Log.d("chatRoomAdd", "success")
                        Toast.makeText(
                            applicationContext,
                            "Successfully add your Chat Room",
                            Toast.LENGTH_SHORT)
                            .show()
                        finish()
                        var intent = Intent(applicationContext, ChatLogActivity::class.java)
                        startActivity(intent)

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed to Complete Chat Room Creation",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        var intent = Intent(applicationContext, CreateChatRoomActivity::class.java)
                        startActivity(intent)
                    }

            }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Failed to add your Chat Room",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                var intent = Intent(applicationContext, CreateChatRoomActivity::class.java)
                startActivity(intent)
            }
    }

}
