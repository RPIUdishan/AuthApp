@file:Suppress("PackageName")

package com.example.authapp.Models

class ChatRoomModel {

    var chatRoomName: String = ""
    var chatRoomDescription: String = ""
    var messageList: ArrayList<String> = ArrayList()
    var userModelList: ArrayList<String> = ArrayList()

    constructor() : this("", "", arrayListOf(), arrayListOf())

    //overloaded constructor
    constructor(chatRoomName: String,
                chatRoomDescription: String,
                messageList: ArrayList<String>,
                userModelList: ArrayList<String>){

        this.chatRoomName = chatRoomName
        this.chatRoomDescription = chatRoomDescription
        this.messageList = messageList
        this.userModelList = userModelList
    }
}
