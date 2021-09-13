package com.example.chatter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.R
import com.example.chatter.models.Inbox
import kotlinx.android.synthetic.main.list_item_inbox.view.*

class InboxAdapter(private val inboxList: MutableList<Inbox>) :
    RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {
    class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder =
        InboxViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_inbox, parent, false)
        )

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        val item=inboxList[position]
        holder.itemView.apply{
            tvContactName.apply{
                text=item.name
                visibility=View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = inboxList.size
}