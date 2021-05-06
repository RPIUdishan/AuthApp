@file:Suppress("PackageName")

package com.example.authapp.Models

class UserModel {
    var email: String = ""
    var username: String = ""
    var profileImageUrl: String = ""
    var userChatRoomModelList: ArrayList<String> = ArrayList()


    //overloaded constructor
    constructor( email: String,
                 username: String,
                 profileImageUrl: String,
                 userChatRoomModelList: ArrayList<String>)
    {
        this.email = email
        this.username = username
        this.profileImageUrl = profileImageUrl
        this.userChatRoomModelList = userChatRoomModelList
    }
}