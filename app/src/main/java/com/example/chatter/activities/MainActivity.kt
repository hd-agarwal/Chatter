package com.example.chatter.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatter.adapters.ScreenSliderAdapter
import com.example.chatter.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var _binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)
        _binding.viewPager.adapter= ScreenSliderAdapter(this)
        TabLayoutMediator(_binding.tabs,_binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Chats"
                1 -> tab.text = "People"
            }
        }.attach()
    }
}