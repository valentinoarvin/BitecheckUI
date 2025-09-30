package com.example.bitechecktest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bitechecktest.databinding.ActivityAddEditFoodBinding

class AddEditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditFoodBinding

    companion object {
        const val EXTRA_FOOD_NAME = "EXTRA_FOOD_NAME"
        const val EXTRA_CALORIES = "EXTRA_CALORIES"
        const val EXTRA_PROTEIN = "EXTRA_PROTEIN"
        const val EXTRA_CARBS = "EXTRA_CARBS"
        const val EXTRA_FAT = "EXTRA_FAT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {

            saveFood()
        }

        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, ScanFoodActivity::class.java)
            startActivity(intent)
        }
        binding.btnAiChat.setOnClickListener {
            val intent = Intent(this, AiChatActivity::class.java)
            startActivity(intent)
        }
    }

    // This function correctly packages the data and sends it back.
    // It was already correct, just wasn't being used.
    private fun saveFood() {
        val foodName = binding.etFoodName.text.toString()
        val calories = binding.etCalories.text.toString().toIntOrNull() ?: 0
        val protein = binding.etProtein.text.toString().toDoubleOrNull() ?: 0.0
        val carbs = binding.etCarbs.text.toString().toDoubleOrNull() ?: 0.0
        val fat = binding.etFat.text.toString().toDoubleOrNull() ?: 0.0

        if (foodName.isBlank()) {
            Toast.makeText(this, "Food name cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_FOOD_NAME, foodName)
        resultIntent.putExtra(EXTRA_CALORIES, calories)
        resultIntent.putExtra(EXTRA_PROTEIN, protein)
        resultIntent.putExtra(EXTRA_CARBS, carbs)
        resultIntent.putExtra(EXTRA_FAT, fat)

        setResult(Activity.RESULT_OK, resultIntent)

        finish()
    }
}