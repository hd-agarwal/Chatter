package com.example.chatter.models

class User (
    var username: String? = null,
    var phone_number: String? = null,
    var status: String? = null,
    var photo_url: String? = null,
    var groups:Array<String>?=null,
    var chats:Array<String>?=null
)