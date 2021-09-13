package com.example.chatter.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatter.R
import com.example.chatter.activities.ChatActivity
import com.example.chatter.activities.IMAGE_URL
import com.example.chatter.activities.NAME
import com.example.chatter.activities.UID
import com.example.chatter.models.User
import com.example.chatter.viewholder.EmptViewHolder
import com.example.chatter.viewholder.UserViewHolder
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val HIDDEN_USER_VIEW = 2
private const val NORMAL_USER_VIEW = 1

class PeopleFragment : Fragment() {
    //    private val people = ArrayList<Person>()
    private val query by lazy {
        FirebaseFirestore.getInstance()
            .collection("users")
            .orderBy("name")
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    //    val options: FirestoreRecyclerOptions<Person> = FirestoreRecyclerOptions.Builder<Person>()
//        .setQuery(query,
//            SnapshotParser {
//                return@SnapshotParser Person(
//                    it.get("name") as String?,
//                    it.get("status") as String?,
//                    it.get("uid") as String?,
//                    it.get("imageUrl") as String?
//                )
//            })
//        .build()
    lateinit var mAdapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>
//    var adapter: FirestoreRecyclerAdapter<*, *> =
//        object : FirestoreRecyclerAdapter<Person, UserViewHolder>(options) {
//            //            fun onBindViewHolder(holder: PersonHolder?, position: Int, model: Person?) {
////                // Bind the Chat object to the ChatHolder
////                // ...
//////            }
////override fun onDataChanged() {
////    // Called each time there is a new query snapshot. You may want to use this method
////    // to hide a loading spinner or check for the "no documents" state and update your UI.
////    // ...
////    Log.d(TAG, "onDataChanged: ")
////}
//            override fun onCreateViewHolder(group: ViewGroup, i: Int): UserViewHolder {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.message for each item
//                val view: View = LayoutInflater.from(group.context)
//                    .inflate(R.layout.layout_user, group, false)
//                return UserViewHolder(view)
//            }
//
//            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: Person) {
//                Log.d("TAG", "onBindViewHolder: " + model.name)
//                holder.tvContactName.apply {
//                    text = model.name
//                    visibility = View.VISIBLE
//                }
//                if (!model.status.isNullOrEmpty()) {
//                    holder.tvStatus.apply {
//                        text = model.status
//                        visibility = View.VISIBLE
//                    }
//                }
//                holder.ivUserPhoto.apply {
//                    Glide.with(requireContext()).load(model.imageUrl)
//                        .placeholder(R.drawable.ic_default_profile_foreground)
//                        .into(this)
//                    visibility = View.VISIBLE
//                }
//            }
//        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpAdapter()
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun setUpAdapter() {
        val config = PagingConfig(10, 2, false)
//            .setEnablePlaceholders(false)
//            .setPageSize(10)
//            .setPrefetchDistance(2)
//            .build()
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(query, config, User::class.java)
            .build()
        mAdapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return when (viewType) {
                    NORMAL_USER_VIEW -> {
                        UserViewHolder(layoutInflater.inflate(R.layout.list_item_inbox, parent, false))
                    }
                    else -> {
                        EmptViewHolder(layoutInflater.inflate(R.layout.layout_empty, parent, false))
                    }
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                model: User
            ) {
                if (holder is UserViewHolder) {
                    holder.bindView(model) { name, uid, imageUrl ->
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(NAME, name)
                        intent.putExtra(UID, uid)
                        intent.putExtra(IMAGE_URL, imageUrl)
                        Log.d("TAG", "onBindViewHolder: Starting chat activity")
                        startActivity(intent)
                    }
                } else {
                }
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return if (auth.currentUser!!.uid == item!!.uid) {
                    HIDDEN_USER_VIEW
                } else {
                    NORMAL_USER_VIEW
                }
            }

        }
        // Activities can use lifecycleScope directly, but Fragments should instead use
// viewLifecycleOwner.lifecycleScope.
        viewLifecycleOwner.lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Error -> {
                        // The initial load failed. Call the retry() method
                        // in order to retry the load operation.
                        // ...

                    }
                    is LoadState.Loading -> {
                        // The initial Load has begun
                        // ...
                    }
                }

                when (loadStates.append) {
                    is LoadState.Error -> {
                        // The additional load failed. Call the retry() method
                        // in order to retry the load operation.
                        // ...
                    }
                    is LoadState.Loading -> {
                        // The adapter has started to load an additional page
                        // ...
                    }
                    is LoadState.NotLoading -> {
                        if (loadStates.append.endOfPaginationReached) {
                            // The adapter has finished loading all of the data set
                            // ...
                        }
                        if (loadStates.refresh is LoadState.NotLoading) {
                            // The previous load (either initial or additional) completed
                            // ...
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        query.addSnapshotListener(object : EventListener<QuerySnapshot?> {
//            override fun onEvent(value: QuerySnapshot?, e: FirebaseFirestoreException?) {
//                if (e != null) {
//                    // Handle error
//                    //...
//                    return
//                }
//
//                // Convert query snapshot to a list of chats
////                val tempPeople: ArrayList<Person> =
//                if (value != null) {
//                    people.addAll(value.toObjects(Person::class.java))
//                    Log.d("TAG", "onEvent: $people")
//                }
//
//                // Update UI
//                // ...
//            }
//
//        })
        rvPeople.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

//    override fun onStart() {
//        super.onStart()
//        adapter.startListening()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        adapter.stopListening()
//    }
}
