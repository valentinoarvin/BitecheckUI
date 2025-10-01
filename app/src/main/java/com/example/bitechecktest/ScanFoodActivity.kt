package com.example.bitechecktest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bitechecktest.databinding.ActivityScanFoodBinding

class ScanFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val navBinding = binding.bottomNavigation
        NavigationHandler.setup(this, navBinding.bottomNavigationView, navBinding.fab)

    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_scan).isChecked = true
    }
}