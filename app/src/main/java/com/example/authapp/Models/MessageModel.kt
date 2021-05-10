@file:Suppress("PackageName")

package com.example.authapp.Models

import com.google.firebase.Timestamp


class MessageModel {

    var message: String = ""
    var sender: String = ""
    var time: String

    constructor(msg: String,
                sender: String){

        this.message = msg
        this.sender = sender
        this.time = Timestamp.now().toString()
    }

    constructor(msg: String,
                sender: String,
                time: String
    ){

        this.message = msg
        this.sender = sender
        this.time = time
    }

}
