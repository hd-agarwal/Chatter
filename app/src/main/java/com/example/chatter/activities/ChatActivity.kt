package com.example.chatter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chatter.R
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.EmojiManager
import kotlinx.android.synthetic.main.activity_chat.*

const val NAME = "name"
const val UID = "uid"
const val IMAGE_URL = "imageUrl"

class ChatActivity : AppCompatActivity() {
    private val friendId by lazy {
        intent.getStringExtra(UID)
    }
    private val friendName by lazy {
        intent.getStringExtra(NAME)
    }
    private val friendImageUrl by lazy {
        intent.getStringExtra(IMAGE_URL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(IosEmojiProvider())
        setContentView(R.layout.activity_chat)
        tvUsername.apply{
            text=friendName
        }
        Glide.with(this).load(friendImageUrl)
            .placeholder(R.drawable.ic_default_profile_foreground)
            .into(ivUserPhoto)
    }
}