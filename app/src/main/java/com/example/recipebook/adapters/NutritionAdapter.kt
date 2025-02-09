package com.example.recipebook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.modelclasses.adapterModels.Nutrition

class NutritionAdapter(val list: List<Nutrition>): RecyclerView.Adapter<NutritionAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.txt_nuration_title)
        val value: TextView = itemView.findViewById(R.id.txt_nuration_title_value)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionAdapter.ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nuration_items,parent,false))
    }

    override fun onBindViewHolder(holder: NutritionAdapter.ViewHolder, position: Int) {
        holder.name.text = list[position].nutritionTitle
        holder.value.text = list[position].nutritionValue

    }

    override fun getItemCount(): Int {
        return list.size
    }

}