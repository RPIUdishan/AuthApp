package com.example.authapp.Models

class User {
    var email: String = ""
    var username: String = ""
    var profileImageUrl: String = ""

    constructor( email: String, username: String, profileImageUrl: String) {
        this.email = email
        this.username = username
        this.profileImageUrl = profileImageUrl
    }
}