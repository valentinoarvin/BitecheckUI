package com.example.bitechecktest

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

object NavigationHandler {
    fun setup(
        activity: Activity,
        bottomNavView: BottomNavigationView,
        fab: FloatingActionButton
    ) {
        bottomNavView.menu.findItem(R.id.nav_placeholder).isEnabled = false

        bottomNavView.setOnItemSelectedListener { item ->
            val currentActivityName = activity::class.java.simpleName

            when (item.itemId) {
                R.id.nav_home -> {
                    if (currentActivityName != "MainActivity") {
                        launchActivity(activity, MainActivity::class.java, true)
                    }
                    true
                }
                R.id.nav_log -> {
                    if (currentActivityName != "FoodLogActivity") {
                        launchActivity(activity, FoodLogActivity::class.java)
                    }
                    true
                }
                R.id.nav_scan -> {
                    if (currentActivityName != "ScanFoodActivity") {
                        launchActivity(activity, ScanFoodActivity::class.java)
                    }
                    true
                }
                R.id.nav_chat -> {
                    if (currentActivityName != "AiChatActivity") {
                        launchActivity(activity, AiChatActivity::class.java)
                    }
                    true
                }
                else -> false
            }
        }

        // The fab.setOnClickListener has been REMOVED from here
    }

    private fun launchActivity(
        currentActivity: Activity,
        targetActivity: Class<*>,
        isHome: Boolean = false
    ) {
        val intent = Intent(currentActivity, targetActivity)
        if (isHome) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        currentActivity.startActivity(intent)
    }
}