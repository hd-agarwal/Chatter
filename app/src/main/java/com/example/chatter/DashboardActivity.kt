package com.example.chatter

import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chatter.databinding.ActivityDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


class DashboardActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_READ_CONTACTS = 32
    }

    private lateinit var _binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
//            GlobalScope.launch(withContext(Dispatchers.IO)) {  }

            thread { logContacts()}
//            Contact.contactsList.clear()
//            Contact.contactsList.addAll(readContacts())
        } else {
            Log.d("TAG", "onViewCreated: calling requestPermissions")
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                ContactsFragment.REQUEST_READ_CONTACTS
            )
        }
        _binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)
        _binding.toolbar.title = getString(R.string.app_name)
        loadFragment(ChatsFragment())
        _binding.dashboardBottomNav.setOnItemSelectedListener{ menuItem ->
            val id = menuItem.itemId
            if (id == R.id.miChats) {
                _binding.toolbar.title = getString(R.string.app_name)+" - Chats"
                loadFragment(ChatsFragment())
                return@setOnItemSelectedListener true
            } else if (id == R.id.miGroups) {
                _binding.toolbar.title = getString(R.string.app_name)+" - Group Chats"
                loadFragment(GroupChatsFragment())
                return@setOnItemSelectedListener true
            } else if (id == R.id.miContacts) {
                _binding.toolbar.title = getString(R.string.app_name)+" - Contacts"
                loadFragment(ContactsFragment())
                return@setOnItemSelectedListener true
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (fragment != null) {
            transaction.replace(R.id.frame_container, fragment)
        }
        transaction.commit()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: inside")
        if (requestCode== ContactsFragment.REQUEST_READ_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "onRequestPermissionsResult: permission is granted")
            thread{logContacts()}
//           Contact.contactsList.clear()
//            Contact.contactsList.addAll(readContacts())
        } else {
            // permission denied,Disable the
            // functionality that depends on this permission.
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }


    }

    private fun logContacts() {
        val start=System.currentTimeMillis()
        val contactsCursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,ContactsContract.Contacts.DISPLAY_NAME + " ASC ")
        if((contactsCursor?.count?:0)>0)
        {
            while(contactsCursor!=null&&contactsCursor.moveToNext())
            {
                val rowID=contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name=contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                var phoneNumber=""
                if(contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))>0)
                {
                    val phoneNumberCursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",arrayOf<String>(rowID),null)
                    if (phoneNumberCursor != null) {
                        while(phoneNumberCursor.moveToNext()) {
                            phoneNumber+=phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"\n"
                        }
                    }
                    phoneNumberCursor?.close()

                }

                Log.d("Contacts",
                    "logContacts: CONTACTID:$rowID NAME:$name PHONENUMBER:$phoneNumber"
                )
            }
        }
        contactsCursor?.close()
        val end=System.currentTimeMillis()
        Log.d("Contacts", "logContacts: time taken=${end-start} ms")
    }
//    private fun readContacts(): ArrayList<Contact> {
//        val contactList: ArrayList<Contact> = ArrayList()
//        val uri: Uri = ContactsContract.Contacts.CONTENT_URI // Contact URI
//        val contactsCursor: Cursor? = contentResolver.query(
//            uri, null, null,
//            null, ContactsContract.Contacts.DISPLAY_NAME + " ASC "
//        ) // Return
//        // all
//        // contacts
//        // name
//        // containing
//        // in
//        // URI
//        // in
//        // ascending
//        // order
//        // Move cursor at starting
//        if (contactsCursor != null) {
//            if (contactsCursor.moveToFirst()) {
//                do {
//                    val contactId: Long = contactsCursor.getLong(
//                        contactsCursor
//                            .getColumnIndex("_ID")
//                    ) // Get contact ID
//                    val dataUri: Uri = ContactsContract.Data.CONTENT_URI // URI to get
//                    // data of
//                    // contacts
//                    val dataCursor: Cursor? = this.contentResolver.query(
//                        dataUri, null,
//                        ContactsContract.Data.CONTACT_ID + " = " + contactId,
//                        null, null
//                    ) // Retrun data cusror represntative to
//                    // contact ID
//
//                    // Strings to get all details
//                    var displayName = ""
//                    var homePhone = ""
//                    var mobilePhone = ""
//                    var workPhone = ""
//
//                    // This strings stores all contact numbers, email and other
//                    // details like nick name, company etc.
////                    var contactNumbers = ""
//
//                    // Now start the cusrsor
//                    if (dataCursor != null) {
//                        if (dataCursor.moveToFirst()) {
//                            displayName = dataCursor
//                                .getString(
//                                    dataCursor
//                                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
//                                ).orEmpty() // get
//                            // the
//                            // contact
//                            // name
//                            do {
//                                if (dataCursor
//                                        .getString(
//                                            dataCursor.getColumnIndex("mimetype")
//                                        )
//                                        .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
//                                ) {
//                                }
//                                if (dataCursor
//                                        .getString(
//                                            dataCursor.getColumnIndex("mimetype")
//                                        )
//                                        .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                                ) {
//
//                                    // In this get All contact numbers like home,
//                                    // mobile, work, etc and add them to numbers string
//                                    when (dataCursor.getInt(
//                                        dataCursor
//                                            .getColumnIndex("data2")
//                                    )) {
//                                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
//                                            homePhone = dataCursor.getString(
//                                                dataCursor
//                                                    .getColumnIndex("data1")
//                                            )
//                                            contactNumbers += ("Home Phone : " + homePhone
//                                                    + "n")
//                                        }
//                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
//                                            workPhone = dataCursor.getString(
//                                                dataCursor
//                                                    .getColumnIndex("data1")
//                                            )
//                                            contactNumbers += ("Work Phone : " + workPhone
//                                                    + "n")
//                                        }
//                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
//                                            mobilePhone = dataCursor.getString(
//                                                dataCursor
//                                                    .getColumnIndex("data1")
//                                            )
//                                            contactNumbers += ("Mobile Phone : "
//                                                    + mobilePhone + "n")
//                                        }
//                                    }
//                                }
//                                if (dataCursor != null) {
//                                    if (dataCursor
//                                            .getString(
//                                                dataCursor.getColumnIndex("mimetype")
//                                            )
//                                            .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                                    ) {
//
//                                        // In this get all Emails like home, work etc and
//                                        // add them to email string
//                                        if (dataCursor != null) {
//                                            when (dataCursor.getInt(
//                                                dataCursor
//                                                    .getColumnIndex("data2")
//                                            )) {
//                                                ContactsContract.CommonDataKinds.Email.TYPE_HOME -> {
//                                                    homeEmail = dataCursor.getString(
//                                                        dataCursor
//                                                            .getColumnIndex("data1")
//                                                    )
//                                                    contactEmailAddresses += ("Home Email : "
//                                                            + homeEmail + "n")
//                                                }
//                                                ContactsContract.CommonDataKinds.Email.TYPE_WORK -> {
//                                                    workEmail = dataCursor.getString(
//                                                        dataCursor
//                                                            .getColumnIndex("data1")
//                                                    )
//                                                    contactEmailAddresses += ("Work Email : "
//                                                            + workEmail + "n")
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                if (dataCursor
//                                        .getString(
//                                            dataCursor.getColumnIndex("mimetype")
//                                        )
//                                        .equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                                ) {
//                                    companyName = dataCursor.getString(
//                                        dataCursor
//                                            .getColumnIndex("data1")
//                                    ).orEmpty()// get company
//                                    // name
//                                    contactOtherDetails += ("Coompany Name : "
//                                            + companyName + "n")
//                                    title = dataCursor.getString(
//                                        dataCursor
//                                            .getColumnIndex("data4")
//                                    ) // get Company
//                                    // title
//                                    contactOtherDetails += "Title : " + title + "n"
//                                }
//                                if (dataCursor != null) {
//                                    if (dataCursor
//                                            .getString(
//                                                dataCursor.getColumnIndex("mimetype")
//                                            )
//                                            .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
//                                    ) {
//                                        if (dataCursor != null) {
//                                            photoByte = dataCursor.getBlob(
//                                                dataCursor
//                                                    .getColumnIndex("data15")
//                                            )
//                                        } // get photo in
//                                        // byte
//                                        if (photoByte != null) {
//
//                                            // Now make a cache folder in file manager to
//                                            // make cache of contacts images and save them
//                                            // in .png
//                                            val bitmap = BitmapFactory.decodeByteArray(
//                                                photoByte, 0, photoByte.size
//                                            )
//                                            val cacheDirectory: File = requireContext()
//                                                .getCacheDir()
//                                            val tmp = File(
//                                                cacheDirectory.getPath()
//                                                    .toString() + "/_androhub" + contactId + ".png"
//                                            )
//                                            try {
//                                                val fileOutputStream = FileOutputStream(
//                                                    tmp
//                                                )
//                                                bitmap.compress(
//                                                    Bitmap.CompressFormat.PNG,
//                                                    100, fileOutputStream
//                                                )
//                                                fileOutputStream.flush()
//                                                fileOutputStream.close()
//                                            } catch (e: Exception) {
//                                                // TODO: handle exception
//                                                e.printStackTrace()
//                                            }
//                                            photoPath = tmp.getPath() // finally get the
//                                            // saved path of
//                                            // image
//                                        }
//                                    }
//                                }
//                            } while (dataCursor.moveToNext()) // Now move to next
//                            // cursor
//                            // Finally add
//                            // items to
//                            // array list
//                        }
//                    }
//                    dataCursor?.close()
//                } while (contactsCursor.moveToNext())
//            }
//        }
//        contactsCursor?.close()
//        return contactList
//    }

}