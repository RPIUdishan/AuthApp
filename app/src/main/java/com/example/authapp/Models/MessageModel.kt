@file:Suppress("PackageName")

package com.example.authapp.Models

import com.google.firebase.Timestamp


class MessageModel {

    var messageID: String = ""
    var message: String = ""
    var sender: String = ""
    lateinit var time: Timestamp

    constructor(msgID: String,
                msg: String,
                sender: String){
        this.messageID = msgID
        this.message = msg
        this.sender = sender
        this.time = Timestamp.now()
    }

}
