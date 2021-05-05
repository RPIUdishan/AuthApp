@file:Suppress("PackageName")

package com.example.authapp.Models

class ChatRoomModel {
    var chatRoomName: String = ""
    var chatRoomDescription: String = ""
    var messageList: ArrayList<MessageModel> = ArrayList()
    var userModelList: ArrayList<String> = ArrayList()

    constructor(chatRoomName: String, chatRoomDescription: String, messageList: ArrayList<MessageModel>, userModelList: ArrayList<String>){
        this.chatRoomName = chatRoomName
        this.chatRoomDescription = chatRoomDescription
        this.messageList = messageList
        this.userModelList = userModelList
    }
}