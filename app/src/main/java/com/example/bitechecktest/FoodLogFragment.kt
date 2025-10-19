package com.example.bitechecktest

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitechecktest.databinding.FragmentFoodLogBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FoodLogFragment : Fragment() {

    private var _binding: FragmentFoodLogBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var historicalLogs: MutableList<DailyLog>
    private lateinit var logAdapter: LogAdapter
    // Duplicate declarations removed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Daily Logs"
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        loadHistoricalLogs()

        // typo corrected
        if (historicalLogs.isEmpty()) {
            binding.recyclerViewLogs.visibility = View.GONE
            binding.tvLogEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewLogs.visibility = View.VISIBLE
            binding.tvLogEmptyState.visibility = View.GONE
        }

        logAdapter = LogAdapter(
            logList = historicalLogs,
            onLogClicked = { dailyLog ->
                showLogDetailsDialog(dailyLog)
            },
            onUndoClicked = {
                if (historicalLogs.isNotEmpty()) {
                    val logToUndo = historicalLogs.removeAt(0)
                    saveHistoricalLogs()
                    sharedViewModel.setUndoneLog(logToUndo)
                    Snackbar.make(binding.root, "Log restored to home screen.", Snackbar.LENGTH_SHORT).show()
                    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.nav_home
                }
            },
            onLogLongClicked = { dailyLog ->
                showDeleteLogConfirmationDialog(dailyLog)
            }
        )
        binding.recyclerViewLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = logAdapter
        }
    }

    private fun loadHistoricalLogs() {
        val sharedPreferences = requireActivity().getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonLogs = sharedPreferences.getString("daily_logs_key", null)
        val logType = object : TypeToken<MutableList<DailyLog>>() {}.type
        historicalLogs = if (jsonLogs != null) {
            gson.fromJson(jsonLogs, logType)
        } else {
            mutableListOf()
        }
    }

    private fun showDeleteLogConfirmationDialog(dailyLog: DailyLog) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Log")
            .setMessage("Are you sure you want to delete the log for '${dailyLog.date}'?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Delete") { _, _ ->
                val position = historicalLogs.indexOf(dailyLog)
                if (position != -1) {
                    val removedLog = historicalLogs.removeAt(position)
                    logAdapter.notifyItemRemoved(position)
                    saveHistoricalLogs()
                    Snackbar.make(binding.root, "Log for ${removedLog.date} deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            historicalLogs.add(position, removedLog)
                            logAdapter.notifyItemInserted(position)
                            saveHistoricalLogs()
                        }.show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveHistoricalLogs() {
        val sharedPreferences = requireActivity().getSharedPreferences("BiteCheckPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        editor.putString("daily_logs_key", gson.toJson(historicalLogs))
        editor.apply()
    }

    private fun showLogDetailsDialog(dailyLog: DailyLog) {
        val details = StringBuilder()
        dailyLog.entries.forEach { entry ->
            details.append("• ${entry.name} (${entry.calories} kcal)\n")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Details for ${dailyLog.date}")
            .setMessage(details.toString())
            .setPositiveButton("Close", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}