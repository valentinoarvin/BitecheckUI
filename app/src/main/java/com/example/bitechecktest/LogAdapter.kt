package com.example.bitechecktest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(
    private var logList: List<DailyLog>,
    private val onLogClicked: (DailyLog) -> Unit,
    private val onUndoClicked: () -> Unit,
    private val onLogLongClicked: (DailyLog) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RECENT = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.tvLogDate)
        val caloriesText: TextView = itemView.findViewById(R.id.tvTotalCaloriesLog)
    }

    inner class RecentLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.tvLogDate)
        val caloriesText: TextView = itemView.findViewById(R.id.tvTotalCaloriesLog)
        val undoButton: Button = itemView.findViewById(R.id.btnUndoLog)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_RECENT else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RECENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_log_recent, parent, false)
            RecentLogViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_log, parent, false)
            LogViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dailyLog = logList[position]
        val totalCalories = dailyLog.entries.sumOf { it.calories }

        when (holder) {
            is RecentLogViewHolder -> {
                holder.dateText.text = dailyLog.date
                holder.caloriesText.text = "$totalCalories kcal"
                holder.itemView.setOnClickListener { onLogClicked(dailyLog) }
                holder.itemView.setOnLongClickListener { onLogLongClicked(dailyLog); true }
                holder.undoButton.setOnClickListener { onUndoClicked() }
            }
            is LogViewHolder -> {
                holder.dateText.text = dailyLog.date
                holder.caloriesText.text = "$totalCalories kcal"
                holder.itemView.setOnClickListener { onLogClicked(dailyLog) }
                holder.itemView.setOnLongClickListener { onLogLongClicked(dailyLog); true }
            }
        }
    }

    override fun getItemCount(): Int = logList.size
}