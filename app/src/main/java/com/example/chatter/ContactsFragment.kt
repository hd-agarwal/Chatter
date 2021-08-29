package com.example.chatter

import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatter.adapters.UserAdapter
import com.example.chatter.models.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import java.io.File
import java.io.FileOutputStream


class ContactsFragment : Fragment() {
    companion object {
        const val REQUEST_READ_CONTACTS = 32
    }

    private val usersList = ArrayList<User>()
    private val contactsList = ArrayList<Contact>()
    private val query: Query = FirebaseDatabase.getInstance()
        .reference
        .child("users")
//    val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
//        .setQuery(query, User::class.java)
//        .build()
//    private val adapter: FirebaseRecyclerAdapter<*, *> =
//        object : FirebaseRecyclerAdapter<User, UserHolder>(options) {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.message for each item
//                val v: View = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.layout_contact, parent, false)
//                return UserHolder(v)
//            }
//
//            override fun onBindViewHolder(
//                holder: UserHolder,
//                position: Int,
//                model: User
//            ) {
//                // Bind the Chat object to the ChatHolder
//                // ...
//                Log.d("TAG", "onBindViewHolder: "+model.username)
//                Glide.with(requireContext()).load(model.photo_url)
//                    .placeholder(R.drawable.ic_default_profile_foreground)
//                    .into(holder.ivUserProfile)
//                holder.tvContactName.apply { text=model.username }
//                holder.tvContactPhone.apply{ text=model.phone_number}
//            }
//
//
//        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
//            logContacts()
        } else {
            Log.d("TAG", "onViewCreated: calling requestPermissions")
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }

//        for (contact in contactsList)
//            Log.d(
//                "TAG",
//                "onViewCreated: " + contact.contactID + ";;" + contact.contactEmail + ";;" + contact.contactName + ";;" + contact.contactNumber + ";;" + contact.contactOtherDetails
//            )
//        rvContacts.layoutManager = LinearLayoutManager(requireContext())
//
//        val adapter = UserAdapter(usersList, requireContext())
//
//        rvContacts.adapter = adapter
//
//        val childEventListener: ChildEventListener = object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                // ...
//
////                dataSnapshot.getValue<User>()?.let { users.add(it) }
////                Log.d("TAG", "onChildAdded: "+users)
////                adapter.notifyDataSetChanged()
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                // ...
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                // ...
//            }
//
//            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                // ...
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // ...
//            }
//        }
//        query.addChildEventListener(childEventListener)
//

    }

    //    override fun onStart() {
//        super.onStart()
//        Log.d("TAG", "onStart: ")
//        adapter.startListening()
//    }
//    override fun onStop() {
//        super.onStop()
//        Log.d("TAG", "onStop: ")
//        adapter.stopListening()
//    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        Log.d("TAG", "onRequestPermissionsResult: inside")
        if (requestCode== REQUEST_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "onRequestPermissionsResult: permission is granted")
        } else {
            // permission denied,Disable the
            // functionality that depends on this permission.
            tvPermissionDenied.apply {
                visibility = View.VISIBLE
            }
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }


    }

    private fun readContacts(): ArrayList<Contact> {
        val contactList: ArrayList<Contact> = ArrayList()
        val uri: Uri = ContactsContract.Contacts.CONTENT_URI // Contact URI
        val contactsCursor: Cursor? = context?.contentResolver?.query(
            uri, null, null,
            null, ContactsContract.Contacts.DISPLAY_NAME + " ASC "
        ) // Return
        // all
        // contacts
        // name
        // containing
        // in
        // URI
        // in
        // ascending
        // order
        // Move cursor at starting
        if (contactsCursor != null) {
            if (contactsCursor.moveToFirst()) {
                do {
                    val contctId: Long = contactsCursor.getLong(
                        contactsCursor
                            .getColumnIndex("_ID")
                    ) // Get contact ID
                    val dataUri: Uri = ContactsContract.Data.CONTENT_URI // URI to get
                    // data of
                    // contacts
                    val dataCursor: Cursor? = context?.contentResolver?.query(
                        dataUri, null,
                        ContactsContract.Data.CONTACT_ID + " = " + contctId,
                        null, null
                    ) // Retrun data cusror represntative to
                    // contact ID

                    // Strings to get all details
                    var displayName = ""
                    var nickName = ""
                    var homePhone = ""
                    var mobilePhone = ""
                    var workPhone = ""
                    var photoPath = ""// Photo path
                    var photoByte: ByteArray? = null // Byte to get photo since it will come
                    // in BLOB
                    var homeEmail = ""
                    var workEmail = ""
                    var companyName = ""
                    var title: String? = ""

                    // This strings stores all contact numbers, email and other
                    // details like nick name, company etc.
                    var contactNumbers = ""
                    var contactEmailAddresses = ""
                    var contactOtherDetails = ""

                    // Now start the cusrsor
                    if (dataCursor != null) {
                        if (dataCursor.moveToFirst()) {
                            displayName = dataCursor
                                .getString(
                                    dataCursor
                                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                                ).orEmpty() // get
                            // the
                            // contact
                            // name
                            do {
                                if (dataCursor
                                        .getString(
                                            dataCursor.getColumnIndex("mimetype")
                                        )
                                        .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                                ) {
                                    nickName = dataCursor.getString(
                                        dataCursor
                                            .getColumnIndex("data1")
                                    ) // Get Nick Name
                                    contactOtherDetails += ("NickName : " + nickName
                                            + "n") // Add the nick name to string
                                }
                                if (dataCursor
                                        .getString(
                                            dataCursor.getColumnIndex("mimetype")
                                        )
                                        .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                ) {

                                    // In this get All contact numbers like home,
                                    // mobile, work, etc and add them to numbers string
                                    when (dataCursor.getInt(
                                        dataCursor
                                            .getColumnIndex("data2")
                                    )) {
                                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
                                            homePhone = dataCursor.getString(
                                                dataCursor
                                                    .getColumnIndex("data1")
                                            )
                                            contactNumbers += ("Home Phone : " + homePhone
                                                    + "n")
                                        }
                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
                                            workPhone = dataCursor.getString(
                                                dataCursor
                                                    .getColumnIndex("data1")
                                            )
                                            contactNumbers += ("Work Phone : " + workPhone
                                                    + "n")
                                        }
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
                                            mobilePhone = dataCursor.getString(
                                                dataCursor
                                                    .getColumnIndex("data1")
                                            )
                                            contactNumbers += ("Mobile Phone : "
                                                    + mobilePhone + "n")
                                        }
                                    }
                                }
                                if (dataCursor != null) {
                                    if (dataCursor
                                            .getString(
                                                dataCursor.getColumnIndex("mimetype")
                                            )
                                            .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                    ) {

                                        // In this get all Emails like home, work etc and
                                        // add them to email string
                                        if (dataCursor != null) {
                                            when (dataCursor.getInt(
                                                dataCursor
                                                    .getColumnIndex("data2")
                                            )) {
                                                ContactsContract.CommonDataKinds.Email.TYPE_HOME -> {
                                                    homeEmail = dataCursor.getString(
                                                        dataCursor
                                                            .getColumnIndex("data1")
                                                    )
                                                    contactEmailAddresses += ("Home Email : "
                                                            + homeEmail + "n")
                                                }
                                                ContactsContract.CommonDataKinds.Email.TYPE_WORK -> {
                                                    workEmail = dataCursor.getString(
                                                        dataCursor
                                                            .getColumnIndex("data1")
                                                    )
                                                    contactEmailAddresses += ("Work Email : "
                                                            + workEmail + "n")
                                                }
                                            }
                                        }
                                    }
                                }
                                if (dataCursor
                                        .getString(
                                            dataCursor.getColumnIndex("mimetype")
                                        )
                                        .equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                                ) {
                                    companyName = dataCursor.getString(
                                        dataCursor
                                            .getColumnIndex("data1")
                                    ).orEmpty()// get company
                                    // name
                                    contactOtherDetails += ("Coompany Name : "
                                            + companyName + "n")
                                    title = dataCursor.getString(
                                        dataCursor
                                            .getColumnIndex("data4")
                                    ) // get Company
                                    // title
                                    contactOtherDetails += "Title : " + title + "n"
                                }
                                if (dataCursor != null) {
                                    if (dataCursor
                                            .getString(
                                                dataCursor.getColumnIndex("mimetype")
                                            )
                                            .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                    ) {
                                        if (dataCursor != null) {
                                            photoByte = dataCursor.getBlob(
                                                dataCursor
                                                    .getColumnIndex("data15")
                                            )
                                        } // get photo in
                                        // byte
                                        if (photoByte != null) {

                                            // Now make a cache folder in file manager to
                                            // make cache of contacts images and save them
                                            // in .png
                                            val bitmap = BitmapFactory.decodeByteArray(
                                                photoByte, 0, photoByte.size
                                            )
                                            val cacheDirectory: File = requireContext()
                                                .getCacheDir()
                                            val tmp = File(
                                                cacheDirectory.getPath()
                                                    .toString() + "/_androhub" + contctId + ".png"
                                            )
                                            try {
                                                val fileOutputStream = FileOutputStream(
                                                    tmp
                                                )
                                                bitmap.compress(
                                                    Bitmap.CompressFormat.PNG,
                                                    100, fileOutputStream
                                                )
                                                fileOutputStream.flush()
                                                fileOutputStream.close()
                                            } catch (e: Exception) {
                                                // TODO: handle exception
                                                e.printStackTrace()
                                            }
                                            photoPath = tmp.getPath() // finally get the
                                            // saved path of
                                            // image
                                        }
                                    }
                                }
                            } while (dataCursor.moveToNext()) // Now move to next
                            // cursor
//                            contactList.add(
//                                Contact(
//                                    contctId.toString(),
//                                    displayName, contactNumbers, contactEmailAddresses,
//                                    contactOtherDetails
//                                )
//                            ) // Finally add
                            // items to
                            // array list
                        }
                    }
                    dataCursor?.close()
                } while (contactsCursor.moveToNext())
            }
        }
        contactsCursor?.close()
        return contactList
    }

}