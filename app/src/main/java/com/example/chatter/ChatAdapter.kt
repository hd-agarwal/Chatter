package com.example.chatter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class ChatAdapter(
    private val chats: ArrayList<Chat>,
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val database = FirebaseDatabase.getInstance().reference

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
        val ivUserProfile: ImageView = itemView.findViewById(R.id.ivUserPhoto)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_chat, parent, false)
        )


    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.tvMessage.apply { text = chats[position].content }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        database.child("users").child(chats[position].secondUid).get().addOnSuccessListener {
            holder.tvContactName.apply { text = it.child("username").value.toString() }
            Glide.with(context).load(it.child("photo_url").value.toString())
                .placeholder(R.drawable.ic_default_profile_foreground)
                .into(holder.ivUserProfile)
        }
        holder.tvMessage.apply { text = chats[position].content }
    }

    override fun getItemCount() = chats.size
}