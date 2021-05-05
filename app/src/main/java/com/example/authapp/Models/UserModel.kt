@file:Suppress("PackageName")

package com.example.authapp.Models

class UserModel {
    var email: String = ""
    var username: String = ""
    var profileImageUrl: String = ""

    constructor( email: String, username: String, profileImageUrl: String) {
        this.email = email
        this.username = username
        this.profileImageUrl = profileImageUrl
    }
}