package com.example.chatter.models

class User {
    var username: String? = null
    var status: String? = null
    var phone_number: String? = null
    var photo_url: String? = null
    var groups: Array<String>? = null
    var chats: Array<String>? = null

    constructor() {} // Needed for Firebase
    constructor(username: String?, status: String?, phone_number: String?,photo_url:String?,groups:Array<String>?,chats:Array<String>?) {
        this.username = username
        this.status = status
        this.phone_number = phone_number
        this.photo_url = photo_url
        this.groups = groups
        this.chats = chats
    }
}