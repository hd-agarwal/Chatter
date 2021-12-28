package com.example.chatter.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatter.R
import com.example.chatter.adapters.ChatAdapter
import com.example.chatter.models.*
import com.example.chatter.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.vanniktech.emoji.ios.IosEmojiProvider
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*


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
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var listener:ChildEventListener
    private var unreadFound=false
    private var unreadPoint=-1
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
        btnSend.setOnClickListener {
            etMessage.text?.let{
                if(it.isNotEmpty()){
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
        chatAdapter= ChatAdapter(messages,mCurrentId,getMessagesRef(friendId!!),getInboxRef(mCurrentId,friendId!!))
        rvMessages.apply{
            layoutManager=LinearLayoutManager(this@ChatActivity)
            adapter=chatAdapter
        }
        listenToMessages()
        etMessage.setOnClickListener {
            rvMessages.scrollToPosition(messages.size-1)
        }
    }


    override fun onStop() {
//        markAsRead()
        getMessagesRef(friendId!!).removeEventListener(listener)
        super.onStop()
    }

    override fun onRestart() {
        unreadFound=false
        unreadPoint=-1
        super.onRestart()
    }
    private fun listenToMessages() {
        listener=object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg=snapshot.getValue(Message::class.java)!!
                addMessage(msg)
                markMessageAsRead(msg)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        friendId?.let {
            getMessagesRef(it).orderByKey().addChildEventListener(listener)
        }
    }

    private fun addMessage(msg: Message) {
        if(msg.status== SENT&&msg.senderId!=mCurrentId && !unreadFound)
        {
            messages.add(UnreadHeader())
            unreadFound=true
            unreadPoint=messages.size
        }
        val lastItem=messages.lastOrNull()
        if((lastItem!=null&&!lastItem.sentAt.isSameDayAs(msg.sentAt))||lastItem==null)
            messages.add(DateHeader(msg.sentAt,this))
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size-1)
        rvMessages.scrollToPosition(messages.size-1)
    }
    private fun markMessageAsRead(msg:Message)
    {
        if (msg.status == SENT && msg.senderId != mCurrentId) {
            val eventUpdates: MutableMap<String, Any> = HashMap()
            eventUpdates["status"] = READ
            getMessagesRef(friendId!!).child(msg.messageId).updateChildren(eventUpdates)
        }
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
        val inboxMap= Inbox(message.content,mCurrentId,friendId!!,friendName!!,friendImageUrl, Date(),0)
        friendId?.let { friendId ->
            getInboxRef(mCurrentId, friendId).setValue(inboxMap).addOnSuccessListener {
            getInboxRef(this.friendId!!,mCurrentId).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value=snapshot.getValue(Inbox::class.java)
                    inboxMap.apply{
                        name=currentUser.name
                        imageUrl=currentUser.imageUrl
                        count=1
                        from=mCurrentId
                    }
                    value?.let{
                        inboxMap.count=it.count+1
                    }
                    getInboxRef(this@ChatActivity.friendId!!,mCurrentId).setValue(inboxMap)
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