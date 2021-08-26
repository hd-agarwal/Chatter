package com.example.chatter

import android.os.Bundle
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
        loadFragment(ChatsFragment())
        _binding.dashboardBottomNav.setOnItemSelectedListener{ menuItem ->
            val id = menuItem.itemId
            if (id == R.id.miChats) {
                loadFragment(ChatsFragment())
                return@setOnItemSelectedListener true
            } else if (id == R.id.miGroups) {
                loadFragment(GroupChatsFragment())
                return@setOnItemSelectedListener true
            } else if (id == R.id.miContacts) {
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