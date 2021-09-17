package com.example.chatter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.R
import com.example.chatter.models.*
import com.example.chatter.utils.formatAsTime
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.list_item_chat_sent.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

const val SENT = 1
const val READ = 2

class ChatAdapter(
    private val list: MutableList<ChatEvent>,
    private val mCurrentId: String,
    private val messagesRef: DatabaseReference,
    private val inboxRef: DatabaseReference
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }
        return when (viewType) {
            TEXT_MESSAGE_SENT -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_sent))
            }
            TEXT_MESSAGE_RECEIVED -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_received))
            }
            DATE_HEADER -> {
                DateViewHolder(inflate(R.layout.list_item_date_header))
            }
            UNREAD_HEADER->{
                UnreadViewHolder(inflate(R.layout.list_item_unread_header))
            }
            else -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_received))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val event = list[position]) {
            is Message -> {
                holder.itemView.apply {
                    tvContent.text = event.content
                    tvTime.text = event.sentAt.formatAsTime()
                }
//                if (event.status == SENT && event.senderId != mCurrentId) {
//                    event.status = READ
//                    val eventUpdates: MutableMap<String, Any> = HashMap()
//                    eventUpdates["status"] = READ
//                    messagesRef.child(event.messageId).updateChildren(eventUpdates)
//                }
            }
            is DateHeader -> {
                holder.itemView.tvDate.text = event.date
            }
            is UnreadHeader->{

            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is DateHeader -> DATE_HEADER
            is Message -> {
                if (event.senderId == mCurrentId)
                    TEXT_MESSAGE_SENT
                else
                    TEXT_MESSAGE_RECEIVED
            }
            is UnreadHeader->{
                UNREAD_HEADER
            }
            else -> UNSUPPORTED
        }
    }

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
        private const val UNREAD_HEADER = 3
    }

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class UnreadViewHolder(view: View) : RecyclerView.ViewHolder(view)
}