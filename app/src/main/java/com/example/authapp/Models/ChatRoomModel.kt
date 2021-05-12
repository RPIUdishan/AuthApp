@file:Suppress("PackageName")

package com.example.authapp.Models

class ChatRoomModel {

    var chatRoomName: String = ""
    var chatRoomDescription: String = ""
    var chatRoomPic: String = ""
    var messageList: ArrayList<MessageModel> = ArrayList()
    var userModelList: ArrayList<String> = ArrayList()

//    constructor() : this("", "", arrayListOf(), arrayListOf())

    //overloaded constructor
    constructor(chatRoomName: String,
                chatRoomDescription: String,
                chatRoomPic: String){

        this.chatRoomName = chatRoomName
        this.chatRoomDescription = chatRoomDescription
        this.chatRoomPic = chatRoomPic
    }

    public fun addUsersToUserList(userId: String){
        this.userModelList.add(userId)
    }

    public fun addMessagesToMessageList(message: MessageModel){
        this.messageList.add(message)
    }
}
