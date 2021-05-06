@file:Suppress("PackageName")

package com.example.authapp.Models



class MessageModel {

    var messageID: String = ""
    var message: String = ""
    var sender: String = ""

    constructor(msgID: String,
                msg: String,
                sender: String){
        this.messageID = msgID
        this.message = msg
        this.sender = sender
    }

}
