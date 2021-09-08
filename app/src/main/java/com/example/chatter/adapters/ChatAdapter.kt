package com.example.chatter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.R
import com.example.chatter.models.ChatEvent
import com.example.chatter.models.DateHeader
import com.example.chatter.models.Message
import com.example.chatter.utils.formatAsTime
import kotlinx.android.synthetic.main.list_item_chat_sent.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(private val list: MutableList<ChatEvent>, private val mCurrentId: String) :
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
            }
            is DateHeader -> {
                holder.itemView.tvDate.text = event.date
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
            else -> UNSUPPORTED
        }
    }

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view)
}