package com.example.chatter.models

import java.util.*

data class Inbox(
    val message: String,
    val from: String,
    var name: String,
    var imageUrl: String?,
    val time: Date,
    var count: Int
) {
    constructor() : this("", "", "", "", Date(), 0)
}