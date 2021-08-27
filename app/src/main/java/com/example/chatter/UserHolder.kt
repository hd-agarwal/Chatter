package com.example.chatter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var ivUserProfile=itemView.findViewById<ImageView>(R.id.ivUserPhoto)
    var tvContactName=itemView.findViewById<TextView>(R.id.tvContactName)
    var tvContactPhone=itemView.findViewById<TextView>(R.id.tvContactPhone)
}
