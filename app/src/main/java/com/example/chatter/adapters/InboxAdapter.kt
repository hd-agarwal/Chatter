package com.example.chatter.adapters

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatter.R
import com.example.chatter.models.Inbox
import com.example.chatter.utils.formatAsHeader
import com.example.chatter.utils.formatAsTime
import kotlinx.android.synthetic.main.list_item_inbox.view.*

class InboxAdapter(
    private val inboxList: MutableList<Inbox>,
    private val currentUid: String,
    private val mContext: Context,
    private val onClick: (name: String, uid: String, imageUrl: String?) -> Unit
) :
    RecyclerView.Adapter<InboxAdapter.InboxViewHolder>() {
    class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(
            item: Inbox,
            mContext: Context,
            currentUid: String,
            onClick: (name: String, uid: String, imageUrl: String?) -> Unit
        ) = with(itemView) {
            tvContactName.apply {
                text = item.name
                visibility = View.VISIBLE
            }
            Glide.with(this).load(item.imageUrl)
                .placeholder(R.mipmap.ic_default_profile)
                .into(ivUserPhoto)
            ivUserPhoto.apply { visibility = View.VISIBLE }
            tvStatus.apply {
                var mess = ""
                mess += if (item.senderId == currentUid)
                    "You: "
                else
                    "${item.name}: "
                mess += item.message
                text = mess
                visibility = View.VISIBLE
            }
            tvTime.apply {
                text = if (DateUtils.isToday(item.time.time))
                    item.time.formatAsTime()
                else
                    item.time.formatAsHeader(mContext)
                visibility = View.VISIBLE
            }
            tvCount.apply {
                text = item.count.toString()
                visibility = if (item.count > 0)
                    View.VISIBLE
                else
                    View.GONE
            }
            setOnClickListener {
                onClick.invoke(item.name, item.from, item.imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder =
        InboxViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_inbox, parent, false)
        )

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        val item = inboxList[position]
        holder.bindView(item, mContext, currentUid, onClick)
    }

    override fun getItemCount(): Int = inboxList.size
}