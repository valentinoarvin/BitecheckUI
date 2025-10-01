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

        // Load the HomeFragment by default when the app starts
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
        }

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        // This logic is now handled by the fragment's own lifecycle
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupNavigation() {
        val navBinding = binding.bottomNavigation
        // The placeholder is for spacing and is not clickable
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

        // The FAB still launches an Activity, which is fine!
        navBinding.fab.setOnClickListener {
            val intent = Intent(this, AddEditFoodActivity::class.java)
            // NOTE: The result will now be handled by HomeFragment
            // We need a way to launch this from the fragment. For now, this will
            // add the food, but the list will refresh when you navigate back to home.
            startActivity(intent)
        }
    }
}