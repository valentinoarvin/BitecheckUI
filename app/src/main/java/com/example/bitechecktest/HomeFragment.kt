package com.example.bitechecktest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitechecktest.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AppCompatActivity

class HomeFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter
    private val foodList = mutableListOf<FoodEntry>()

    val addFoodResultLauncher = registerForActivityResult(
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
            saveData()
            updateUI()
            foodAdapter.notifyItemInserted(foodList.size - 1)
            Snackbar.make(binding.root, "$name logged successfully!", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "BiteCheck"

        setupRecyclerView()
        setupPieChart()
        loadData()
        updateUI()

        binding.btnLogToday.setOnClickListener {
            if (foodList.isNotEmpty()) {
                logTodaysEntries()
            }
        }

        sharedViewModel.undoneLog.observe(viewLifecycleOwner) { undoneLog ->
            if (undoneLog != null) {
                foodList.clear()
                foodList.addAll(undoneLog.entries)
                saveData()
                updateUI()
                foodAdapter.notifyDataSetChanged()

                sharedViewModel.consumeUndoneLog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logTodaysEntries() {
        val sdf = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val newLog = DailyLog(date = currentDate, entries = ArrayList(foodList))

        val sharedPreferences = requireActivity().getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonLogs = sharedPreferences.getString("daily_logs_key", null)
        val logType = object : TypeToken<MutableList<DailyLog>>() {}.type
        val historicalLogs: MutableList<DailyLog> = if (jsonLogs != null) {
            gson.fromJson(jsonLogs, logType)
        } else {
            mutableListOf()
        }

        historicalLogs.add(0, newLog)
        val editor = sharedPreferences.edit()
        editor.putString("daily_logs_key", gson.toJson(historicalLogs))
        editor.apply()

        foodList.clear()
        foodAdapter.notifyDataSetChanged()
        saveData()
        updateUI()

        Snackbar.make(binding.root, "Today's entries logged!", Snackbar.LENGTH_SHORT).show()
    }


    private fun saveData() {
        val sharedPreferences = requireActivity().getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(foodList)
        editor.putString("food_list_key", json)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = requireActivity().getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("food_list_key", null)
        val type = object : TypeToken<MutableList<FoodEntry>>() {}.type

        if (json != null) {
            val loadedList: MutableList<FoodEntry> = gson.fromJson(json, type)
            foodList.clear()
            foodList.addAll(loadedList)
        }
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(foodList) { foodEntry ->
            showDeleteConfirmationDialog(foodEntry)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodAdapter
        }
    }

    private fun showDeleteConfirmationDialog(foodEntry: FoodEntry) {
        AlertDialog.Builder(requireContext())
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
        binding.btnLogToday.visibility = View.GONE
    } else {
        binding.tvEmptyState.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.btnLogToday.visibility = View.VISIBLE
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
                Color.parseColor("#FF0000")  // Red for Fat
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