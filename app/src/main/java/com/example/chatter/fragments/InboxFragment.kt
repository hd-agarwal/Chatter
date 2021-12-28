package com.example.chatter.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatter.R
import com.example.chatter.activities.ChatActivity
import com.example.chatter.adapters.InboxAdapter
import com.example.chatter.models.Inbox
import com.example.chatter.utils.IMAGE_URL
import com.example.chatter.utils.NAME
import com.example.chatter.utils.UID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_inbox.*

class InboxFragment : Fragment() {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val query by lazy {
        auth.currentUser?.let {
            FirebaseDatabase.getInstance().reference.child(getString(R.string.db_chats)).child(
                it.uid
            )
        }
    }
    private val inboxList = mutableListOf<Inbox>()
    lateinit var mAdapter: InboxAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    private fun setupAdapter() {
        mAdapter = InboxAdapter(inboxList, auth.uid!!, requireContext()) { name, uid, imageUrl ->
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra(NAME, name)
            intent.putExtra(UID, uid)
            intent.putExtra(IMAGE_URL, imageUrl)
            Log.d("TAG", "onBindViewHolder: Starting chat activity")
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvInbox.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        query!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue(Inbox::class.java)
                if (item != null) {
                    var i = inboxList.size - 1
                    while (i >= 0 && item.time > inboxList[i].time) {
                        i--
                    }
                    inboxList.add(i + 1, item)
                    mAdapter.notifyItemInserted(i + 1)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val item = snapshot.getValue(Inbox::class.java)
                if (item != null) {
                    var i=0
                    while(inboxList[i].from!=item.from)
                        i++
                    inboxList[i]=item
                    mAdapter.notifyItemChanged(i)
                    val idx=i
                    while(i>0&&inboxList[i-1].time<item.time)
                    {
                        inboxList[i] = inboxList[i-1]
                        i--
                    }
                    inboxList[i] = item
                    mAdapter.notifyItemMoved(idx,i)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}
