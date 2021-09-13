package com.example.chatter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatter.R
import com.example.chatter.adapters.ChatAdapter
import com.example.chatter.models.*
import com.example.chatter.utils.KeyboardVisibilityUtil
import com.example.chatter.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

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
    private val mCurrentId by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val database by lazy {
        FirebaseDatabase.getInstance().reference
    }
    lateinit var currentUser: User
    private val messages= mutableListOf<ChatEvent>()
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    lateinit var chatAdapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(IosEmojiProvider())
        setContentView(R.layout.activity_chat)
        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            rvMessages.scrollToPosition(messages.size - 1)
        }
        val emojiPopup=EmojiPopup.Builder.fromRootView(rootView).build(etMessage)
        btnEmoji.setOnClickListener {
            emojiPopup.toggle()
        }

        tvUsername.apply {
            text = friendName
        }
        Glide.with(this).load(friendImageUrl)
            .placeholder(R.drawable.ic_default_profile_foreground)
            .into(ivUserPhoto)
        FirebaseFirestore.getInstance().collection(getString(R.string.fr_users)).document(mCurrentId).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }
        markAsRead()
        btnSend.setOnClickListener {
            etMessage.text?.let{
                if(it.isNotEmpty()){
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
        chatAdapter= ChatAdapter(messages,mCurrentId)
        rvMessages.apply{
            layoutManager=LinearLayoutManager(this@ChatActivity)
            adapter=chatAdapter
        }
        listenToMessages()
    }

    private fun listenToMessages() {
        friendId?.let {
            getMessagesRef(it).orderByKey().addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg=snapshot.getValue(Message::class.java)!!
                    addMessage(msg)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    private fun addMessage(msg: Message) {
        val lastItem=messages.lastOrNull()
        if((lastItem!=null&&!lastItem.sentAt.isSameDayAs(msg.sentAt))||lastItem==null)
            messages.add(DateHeader(msg.sentAt,this))
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size-1)
        rvMessages.scrollToPosition(messages.size-1)
    }

    private fun markAsRead() {
        friendId?.let {
            getInboxRef(mCurrentId,
                it
            ).child(getString(R.string.db_chats_count)).setValue(0)
        }
    }
    private fun sendMessage(message: String) {
        val msgId= friendId?.let { getMessagesRef(it).push().key }
        checkNotNull(msgId){"Cannot be null"}
        val msgMap= Message(message,msgId,mCurrentId)
        friendId?.let { getMessagesRef(it).child(msgId).setValue(msgMap).addOnSuccessListener {

        }
            .addOnFailureListener {

            }}
        updateLastMessage(msgMap)

    }

    private fun updateLastMessage(message: Message) {
        val inboxMap= Inbox(message.content,mCurrentId,friendName!!,friendImageUrl, Date(),0)
        friendId?.let { getInboxRef(mCurrentId, it).setValue(inboxMap).addOnSuccessListener {
            getInboxRef(friendId!!,mCurrentId).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value=snapshot.getValue(Inbox::class.java)
                    inboxMap.apply{
                        name=currentUser.name
                        imageUrl=currentUser.imageUrl
                        count=1
                    }
                    value?.let{
                        inboxMap.count=it.count+1
                    }
                    getInboxRef(friendId!!,mCurrentId).setValue(inboxMap)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        } }
    }

    private fun getId(friendId: String): String {
        return if (friendId > mCurrentId)
            mCurrentId + friendId
        else
            friendId + mCurrentId
    }

    private fun getInboxRef(to: String, from: String) = database.child("${getString(R.string.db_chats)}/$to/$from")
    private fun getMessagesRef(friendId: String) = database.child("${getString(R.string.db_messages)}/${getId(friendId)}")
}