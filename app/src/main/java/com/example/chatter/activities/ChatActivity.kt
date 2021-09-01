package com.example.chatter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatter.R

const val NAME="name"
const val UID="uid"
const val IMAGE_URL="imageUrl"
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}