package com.example.chatter.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatter.fragments.InboxFragment
import com.example.chatter.fragments.PeopleFragment

class ScreenSliderAdapter(fa:FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount()=2

    override fun createFragment(position: Int): Fragment =when(position){
        0-> InboxFragment()
        else-> PeopleFragment()
    }

}