package com.example.chatter

import java.sql.Timestamp

class Chat(
    val uid: String,
    var time: Timestamp,
    var content: String,
    var sender: String,
    val secondUid: String,
)