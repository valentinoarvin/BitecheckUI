package com.example.bitechecktest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bitechecktest.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.widget.ImageView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel: SharedViewModel by viewModels()

    private val addFoodLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Unpack the new food data
            val data: Intent? = result.data
            val name = data?.getStringExtra(AddEditFoodActivity.EXTRA_FOOD_NAME) ?: "No Name"
            val calories = data?.getIntExtra(AddEditFoodActivity.EXTRA_CALORIES, 0) ?: 0
            val protein = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_PROTEIN, 0.0) ?: 0.0
            val carbs = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_CARBS, 0.0) ?: 0.0
            val fat = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_FAT, 0.0) ?: 0.0
            val newFood = FoodEntry(name, calories, protein, carbs, fat)

            // Load the current list, add the new item, and save it back to storage
            saveFoodInBackground(newFood)
            Snackbar.make(binding.fragmentContainer, "$name logged successfully!", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
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

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        val logoutItem = menu.findItem(R.id.action_logout)

        val logoutButton = logoutItem.actionView?.findViewById<ImageView>(R.id.iv_logout)

        logoutButton?.setOnClickListener {
            logout()
        }

        return true
    }

    private fun logout() {
        // Navigate back to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)

        // IMPORTANT: These flags clear the entire back stack, so the user can't
        // press the back button to get back into the app after logging out.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
            val intent = Intent(this, AddEditFoodActivity::class.java)

            if (currentFragment is HomeFragment) {
                // If on Home, use the fragment's launcher for an instant UI update
                currentFragment.addFoodResultLauncher.launch(intent)
            } else {
                // If on any other screen, use the activity's new launcher
                addFoodLauncher.launch(intent)
            }
        }
    }

    private fun saveFoodInBackground(foodEntry: FoodEntry) {
        val sharedPreferences = getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("food_list_key", null)
        val type = object : TypeToken<MutableList<FoodEntry>>() {}.type
        val foodList: MutableList<FoodEntry> = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }

        foodList.add(foodEntry)

        val editor = sharedPreferences.edit()
        editor.putString("food_list_key", gson.toJson(foodList))
        editor.apply()
    }
}
