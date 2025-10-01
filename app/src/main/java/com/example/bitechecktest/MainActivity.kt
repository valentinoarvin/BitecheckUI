package com.example.bitechecktest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bitechecktest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
        }

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupNavigation() {
        val navBinding = binding.bottomNavigation
        navBinding.bottomNavigationView.menu.findItem(R.id.nav_placeholder).isEnabled = false

        navBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_log -> FoodLogFragment()
                R.id.nav_scan -> ScanFragment()
                R.id.nav_chat -> AiChatFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }

        navBinding.fab.setOnClickListener {
            val intent = Intent(this, AddEditFoodActivity::class.java)
            startActivity(intent)
        }
    }
}