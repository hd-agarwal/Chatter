package com.example.chatter

import android.os.Bundle
import android.os.health.TimerStat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatter.adapters.ChatAdapter
import com.example.chatter.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chats.*
import java.sql.Timestamp

class ChatsFragment : Fragment() {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance().reference
    private val query: Query = database
        .child("chats")
        .orderByChild("meta_data/last_message/time")

    //        .limitToLast(-1)
    val chats = ArrayList<Chat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (uid == null) {
            Toast.makeText(requireContext(), "An error ocurred", Toast.LENGTH_SHORT).show()
        } else {
            rvChats.layoutManager=LinearLayoutManager(requireContext())
            val adapter=ChatAdapter(chats,requireContext())
            rvChats.adapter=adapter
            val childEventListener: ChildEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    // ...
                    Log.d("TAG", "onChildAdded: " + dataSnapshot.key)
                    if (dataSnapshot.key.toString().startsWith(uid) || dataSnapshot.key.toString()
                            .endsWith(uid)
                    ) {
                        Log.e("TAG", "onChildAdded: "+dataSnapshot.child("meta_data/last_message/time").value.toString())
                        chats.add(0,
                            Chat(
                                dataSnapshot.key.toString(),
                                Timestamp.valueOf(dataSnapshot.child("meta_data/last_message/time").value.toString()),
                                dataSnapshot.child("meta_data/last_message/content").value.toString(),
                                dataSnapshot.child("meta_data/last_message/sender").value.toString(),
                                if (dataSnapshot.key.toString().startsWith(uid)) {
                                    getSecondHalfUid(dataSnapshot.key.toString())
                                } else {
                                    getFirstHalfUid(dataSnapshot.key.toString())
                                }
                            )
                        )
//                        chats.sortedWith(compareBy { it.time })
//                        chats.reverse()
                        adapter.notifyItemInserted(0)
                    }
                }

                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    // ...
                    val chatId=dataSnapshot.key.toString()
                    var i=0
                    while(i<chats.size) {
                        if (chats[i].uid == chatId) {
                            if(chats[i].content!= dataSnapshot.child("meta_data/last_message/content").value.toString())
                                chats[i].content= dataSnapshot.child("meta_data/last_message/content").value.toString()
                            if(chats[i].sender!=dataSnapshot.child("meta_data/last_message/sender").value.toString())
                                chats[i].sender=dataSnapshot.child("meta_data/last_message/sender").value.toString()
                            if(chats[i].time!=Timestamp.valueOf(dataSnapshot.child("meta_data/last_message/time").value.toString()))
                            chats[i].time=Timestamp.valueOf(dataSnapshot.child("meta_data/last_message/time").value.toString())
                            break
                        }
                        i++
                    }
                    adapter.notifyItemChanged(i, mutableListOf(chats[i].secondUid,chats[i].uid))
//                    chats.sortedWith(compareBy { it.time })
//                    chats.reverse()
                    val prevPos=i
                    while(i!=0&&chats[i-1].time<chats[i].time)
                    {
                        val temp=chats[i]
                        chats[i]=chats[i-1]
                        chats[i-1]=temp
                        i--
                    }
                    adapter.notifyItemMoved(prevPos,i)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }


                override fun onCancelled(databaseError: DatabaseError) {
                    // ...
                }
            }
            query.addChildEventListener(childEventListener)

        }
    }

    private fun getSecondHalfUid(completeUid: String): String {
        var i = 0
        while (completeUid[i] != '_')
            i++
        return completeUid.substring(i + 1)
    }

    private fun getFirstHalfUid(completeUid: String): String {
        var i = 0
        while (completeUid[i] != '_')
            i++
        return completeUid.substring(0, i)
    }
}