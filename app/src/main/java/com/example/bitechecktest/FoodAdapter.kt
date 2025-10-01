package com.example.bitechecktest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bitechecktest.databinding.ItemFoodEntryBinding

class FoodAdapter(
    private var foodList: List<FoodEntry>,
    private val onDeleteClicked: (FoodEntry) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: ItemFoodEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.binding.tvFoodName.text = food.name
        holder.binding.tvCalories.text = "${food.calories} kcal"
        holder.binding.tvMacros.text = "P: ${food.protein}g  C: ${food.carbs}g  F: ${food.fat}g"

        holder.itemView.setOnLongClickListener {
            onDeleteClicked(food)
            true
        }
    }

    override fun getItemCount(): Int = foodList.size
}