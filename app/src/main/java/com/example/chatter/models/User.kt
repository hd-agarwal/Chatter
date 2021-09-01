package com.example.chatter.models

import java.util.*

data class User(
    public val name: String,
    public val imageUrl: String?,
    public val thumbUrl: String?,
    public val uid: String,
    public val status: String?,
    public val deviceToken: String,
    public val lastOnline: Date,
    public val onlineStatus:String
) {
    constructor() : this("", "", "", "", "", "", Date(System.currentTimeMillis()),"online")
    constructor(
        name: String,
        imageUrl: String?,
        thumbUrl: String?,
        uid: String,
        status: String?
    ) : this(name, imageUrl, thumbUrl, uid, status, "", Date(System.currentTimeMillis()),"online")
}