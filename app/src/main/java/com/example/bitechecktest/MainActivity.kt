package com.example.bitechecktest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitechecktest.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var foodAdapter: FoodAdapter
    private val foodList = mutableListOf<FoodEntry>()

    private val addFoodResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val name = data?.getStringExtra(AddEditFoodActivity.EXTRA_FOOD_NAME) ?: "No Name"
            val calories = data?.getIntExtra(AddEditFoodActivity.EXTRA_CALORIES, 0) ?: 0
            val protein = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_PROTEIN, 0.0) ?: 0.0
            val carbs = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_CARBS, 0.0) ?: 0.0
            val fat = data?.getDoubleExtra(AddEditFoodActivity.EXTRA_FAT, 0.0) ?: 0.0

            val newFood = FoodEntry(name, calories, protein, carbs, fat)
            foodList.add(newFood)
            foodAdapter.notifyItemInserted(foodList.size - 1)

            updateUI()
            saveData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupPieChart()
        loadData()
        updateUI()

        val navBinding = binding.bottomNavigation
        NavigationHandler.setup(this, navBinding.bottomNavigationView, navBinding.fab)

        navBinding.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true

        navBinding.fab.setOnClickListener {
            val intent = Intent(this, AddEditFoodActivity::class.java)
            addFoodResultLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val json = sharedPreferences.getString("food_list_key", null)

        val type = object : TypeToken<MutableList<FoodEntry>>() {}.type

        if (json != null) {
            val loadedList: MutableList<FoodEntry> = gson.fromJson(json, type)
            foodList.clear()
            foodList.addAll(loadedList)
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(foodList)

        editor.putString("food_list_key", json)
        editor.apply()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(foodList) { foodEntry ->
            showDeleteConfirmationDialog(foodEntry)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = foodAdapter
        }
    }

    private fun showDeleteConfirmationDialog(foodEntry: FoodEntry) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete '${foodEntry.name}'?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Delete") { _, _ ->
                val position = foodList.indexOf(foodEntry)
                if (position != -1) {
                    val removedFood = foodList.removeAt(position)
                    foodAdapter.notifyItemRemoved(position)

                    updateUI()
                    saveData()

                    Snackbar.make(binding.root, "${removedFood.name} deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            foodList.add(position, removedFood)
                            foodAdapter.notifyItemInserted(position)

                            updateUI()
                            saveData()
                        }.show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUI() {
        updateSummary()
        checkEmptyState()
    }

    private fun checkEmptyState() {
        if (foodList.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun updateSummary() {
        val totalCalories = foodList.sumOf { it.calories }
        val totalProtein = foodList.sumOf { it.protein }
        val totalCarbs = foodList.sumOf { it.carbs }
        val totalFat = foodList.sumOf { it.fat }

        binding.tvTotalCalories.text = "$totalCalories kcal"

        val entries = ArrayList<PieEntry>()
        if (totalProtein > 0) entries.add(PieEntry(totalProtein.toFloat(), "Protein"))
        if (totalCarbs > 0) entries.add(PieEntry(totalCarbs.toFloat(), "Carbs"))
        if (totalFat > 0) entries.add(PieEntry(totalFat.toFloat(), "Fat"))

        if (entries.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            binding.llChartPlaceholder.visibility = View.VISIBLE
            binding.pieChart.clear()
        } else {
            binding.pieChart.visibility = View.VISIBLE
            binding.llChartPlaceholder.visibility = View.GONE

            val dataSet = PieDataSet(entries, "Macros")
            dataSet.colors = listOf(
                Color.parseColor("#FFB74D"), // Orange for Protein
                Color.parseColor("#AED581"), // Green for Carbs
                Color.parseColor("#4FC3F7")  // Blue for Fat
            )

            dataSet.sliceSpace = 3f

            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 12f
            dataSet.valueFormatter = PercentFormatter(binding.pieChart)

            val pieData = PieData(dataSet)
            binding.pieChart.data = pieData
            binding.pieChart.setUsePercentValues(true)

            binding.pieChart.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

            binding.pieChart.invalidate()
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            setHoleColor(Color.TRANSPARENT)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setTouchEnabled(false)

            centerText = "Macros"
            setCenterTextColor(Color.parseColor("#A0A0A0")) // text_secondary color
            setCenterTextSize(10f)

            // pie chart animation
            animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
        }
    }

}