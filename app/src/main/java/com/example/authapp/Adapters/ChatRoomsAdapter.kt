@file:Suppress("PackageName")

package com.example.authapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.R
import kotlinx.android.synthetic.main.chat_room_item.view.*

internal class ChatRoomsAdapter(private var chatRoomsList: ArrayList<ChatRoomModel>):
    RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomViewHolder>()
{
    inner class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var chatRoomItemName: TextView = view.textViewChatRoomName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val itemViewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_room_item, parent, false)
        return ChatRoomViewHolder(itemViewHolder)
    }

    override fun onBindViewHolder(holder: ChatRoomsAdapter.ChatRoomViewHolder, position: Int) {

        val item: ChatRoomModel = chatRoomsList[position]
        holder.chatRoomItemName.text = item.chatRoomName

    }

    override fun getItemCount(): Int {
        return chatRoomsList.size
    }


}