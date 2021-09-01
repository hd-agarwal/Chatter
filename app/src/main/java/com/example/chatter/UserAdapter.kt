package com.example.chatter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatter.models.User

class UserAdapter(
    private val usersList:ArrayList<User>,
    val context: Context
): RecyclerView.Adapter<UserAdapter.UserHolder>() {
    class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContactName: TextView =itemView.findViewById(R.id.tvContactName)
        val ivUserProfile: ImageView =itemView.findViewById(R.id.ivUserPhoto)
        val tvStatus: TextView =itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_user, parent, false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        Glide.with(context).load(usersList[position].imageUrl)
                    .placeholder(R.drawable.ic_default_profile_foreground)
                    .into(holder.ivUserProfile)
                holder.tvContactName.apply { text=usersList[position].name }
//                holder.tvStatus.apply{ text=usersList[position].phone_number}
    }

    override fun getItemCount()=usersList.size
}