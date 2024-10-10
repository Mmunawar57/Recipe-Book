package com.example.recipebook.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipebook.R
import com.example.recipebook.modelclasses.RecipeItemList

class RecipeListAdapter(private val context: Context, private val list: List<RecipeItemList>, private val clickFun:(Int)->Unit) :
    RecyclerView.Adapter<RecipeListAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgIcon: ImageView = itemView.findViewById(R.id.img_recipe)
        private val txtTitle: TextView = itemView.findViewById(R.id.txt_recipeTitle)
        private val txtIntergents: TextView = itemView.findViewById(R.id.txt_recipeIntergents)
        private val txtCalories:TextView=itemView.findViewById(R.id.txt_recipeCalories)
        fun bind(item: RecipeItemList) {
            txtTitle.text = item.recipeTitle
            txtIntergents.text = item.intergents
            txtCalories.text=item.calories
            Glide.with(context).load(item.recipeImage).placeholder(R.drawable.loading).into(imgIcon)
        }



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recipe_list,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickFun.invoke(position)
        }
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
      return list.size
    }
}