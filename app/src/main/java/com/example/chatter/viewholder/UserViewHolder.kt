package com.example.chatter.viewholder


import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatter.R
import com.example.chatter.models.User
import kotlinx.android.synthetic.main.list_item_inbox.view.*


class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //    fun bindToGig(gig: Gig, clickListener: View.OnClickListener?) {
//        tvArtist.setText(gig.getArtist())
//        tvVenue.setText(gig.getVenue())
//        tvDate.setText(gig.getDate())
//        tvArtist.setOnClickListener(clickListener)
//    }
    fun bindView(user: User,onClick:(name:String,uid:String,imageUrl:String?)->Unit) = with(itemView) {
        tvContactName.apply {
            text = user.name
            visibility = View.VISIBLE
        }
        if (!user.status.isNullOrEmpty()) {
            tvStatus.apply {
                text = user.status
                visibility = View.VISIBLE
            }
        } else {
            tvStatus.apply {
                visibility = View.GONE
            }
        }
        ivUserPhoto.apply {
            Glide.with(context).load(user.imageUrl)
                .placeholder(R.drawable.ic_default_profile_foreground)
                .into(this)
            visibility = View.VISIBLE
        }
        Log.d("TAG", "bindView: clicklistener set")
        setOnClickListener {

            onClick.invoke(user.name,user.uid,user.imageUrl)
        }
    }

}