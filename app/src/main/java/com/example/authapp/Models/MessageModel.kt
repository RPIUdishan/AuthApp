@file:Suppress("PackageName")

package com.example.authapp.Models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime

class MessageModel {

    var messageID: String = ""
    var message: String = ""
    var sender: String = ""
//    @RequiresApi(Build.VERSION_CODES.O)
//    var time: LocalTime = LocalTime.NOON

//    @RequiresApi(Build.VERSION_CODES.O)
    constructor(msgID: String, msg: String, sender: String){
        this.messageID = msgID
        this.message = msg
        this.sender = sender
//        this.time = LocalTime.now()
    }
}
