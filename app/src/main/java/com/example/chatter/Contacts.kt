package com.example.chatter

internal class Contact {
    var contactName: String? = null
    var contactNumber: String? = null

    companion object {
        var contactsList = ArrayList<Contact>()
    }
}