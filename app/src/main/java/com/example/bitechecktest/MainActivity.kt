package com.example.bitechecktest

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bitechecktest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            // Load HomeFragment by default
            loadFragment(HomeFragment())
        }

        setupNavigation()
        handleIntentNavigation(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentNavigation(intent)
    }

    // This runs every time the activity comes into view, fixing the highlight bug
    override fun onResume() {
        super.onResume()
        // Update the highlight based on the currently visible fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is HomeFragment -> binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
            is FoodLogFragment -> binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_log).isChecked = true
            is ScanFragment -> binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_scan).isChecked = true
            is AiChatFragment -> binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_chat).isChecked = true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun handleIntentNavigation(intent: Intent?) {
        when (intent?.getStringExtra("NAVIGATE_TO")) {
            "SCAN_FRAGMENT" -> {
                loadFragment(ScanFragment())
            }
            "AI_CHAT_FRAGMENT" -> {
                loadFragment(AiChatFragment())
            }
        }
    }

    // The duplicate `loadFragment` function has been REMOVED

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
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is HomeFragment) {
                val intent = Intent(this, AddEditFoodActivity::class.java)
                currentFragment.addFoodResultLauncher.launch(intent)
            } else {
                val intent = Intent(this, AddEditFoodActivity::class.java)
                startActivity(intent)
            }
        }
    }
}