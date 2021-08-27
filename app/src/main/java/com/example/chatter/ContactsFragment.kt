package com.example.chatter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatter.models.User
import com.google.firebase.database.*

import com.google.firebase.database.ktx.getValue
import com.firebase.ui.database.FirebaseRecyclerOptions

import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_contacts.*


class ContactsFragment : Fragment() {

    private val users=ArrayList<User>()
    private val query: Query = FirebaseDatabase.getInstance()
        .reference
        .child("users")
    val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
        .setQuery(query, User::class.java)
        .build()
    private val adapter: FirebaseRecyclerAdapter<*, *> =
        object : FirebaseRecyclerAdapter<User, UserHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val v: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_contact, parent, false)
                return UserHolder(v)
            }

            override fun onBindViewHolder(
                holder: UserHolder,
                position: Int,
                model: User
            ) {
                // Bind the Chat object to the ChatHolder
                // ...
                Log.d("TAG", "onBindViewHolder: "+model.username)
                Glide.with(requireContext()).load(model.photo_url)
                    .placeholder(R.drawable.ic_default_profile_foreground)
                    .into(holder.ivUserProfile)
                holder.tvContactName.apply { text=model.username }
                holder.tvContactPhone.apply{ text=model.phone_number}
            }


        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvContacts.layoutManager= LinearLayoutManager(requireContext())


        rvContacts.adapter=adapter

        val childEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...

//                dataSnapshot.getValue<User>()?.let { users.add(it) }
//                Log.d("TAG", "onChildAdded: "+users)
//                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // ...
            }
        }
        query.addChildEventListener(childEventListener)


    }
    override fun onStart() {
        super.onStart()
        Log.d("TAG", "onStart: ")
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
        adapter.stopListening()
    }

}