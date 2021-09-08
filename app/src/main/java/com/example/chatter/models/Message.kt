package com.example.chatter.models

import android.content.Context
import com.example.chatter.utils.formatAsHeader
import java.util.*

interface ChatEvent {
    val sentAt: Date
}

data class Message(
    val content: String,
    val messageId: String,
    val senderId: String,
    val liked: Boolean = false,
    val status: Int = 1,
    val type: String = "TEXT",
    override val sentAt: Date = Date()
) : ChatEvent {
    constructor() : this("", "", "", false, 1, "", Date())
//    constructor(content:String,msgId:String,senderId:String) : this(content, msgId, senderId, false, 1, "", Date())
}

data class DateHeader(
    override val sentAt:Date,val context: Context
):ChatEvent{
    val date=sentAt.formatAsHeader(context)
}