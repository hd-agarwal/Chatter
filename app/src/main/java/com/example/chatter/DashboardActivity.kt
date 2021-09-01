package com.example.chatter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chatter.databinding.ActivityDashboardBinding


class DashboardActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}