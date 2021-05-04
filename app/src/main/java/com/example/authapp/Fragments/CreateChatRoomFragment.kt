@file:Suppress("PackageName")

package com.example.authapp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.authapp.R
import kotlinx.android.synthetic.main.fragment_dialog_create_chat_room.view.*

class CreateChatRoomFragment: DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            Log.d("asdf0", "asdfasdf")
        }
    return viewRoot
    }
}