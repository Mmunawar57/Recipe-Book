package com.example.recipebook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.recipebook.R
import com.example.recipebook.modelclasses.CategoresList
import com.google.android.material.card.MaterialCardView

class CategoriesAdapter(private val list: List<CategoresList>, val clickFun: (Int) -> Unit) :
    Adapter<CategoriesAdapter.MyViewHolder>() {
        companion object{
            var lastSelectedPosition = 0
        }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cat: TextView = itemView.findViewById(R.id.txt_Categories)
        private val card: MaterialCardView = itemView.findViewById(R.id.materialCardView)
        fun bind(position: Int) {
            cat.text = list[position].title
            if(position==lastSelectedPosition){
                card.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.cardSelectedColor)
            }else{
                card.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.mainColor)
            }
        }
        init {
            itemView.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION) {
                    val previousSelectedPosition = lastSelectedPosition
                    lastSelectedPosition = adapterPosition
                    notifyItemChanged(previousSelectedPosition)
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(adapterPosition)
                    clickFun.invoke(adapterPosition)

                }

            }
        }
    }

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    return MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.categores_list, parent, false)
    )
}

override fun getItemCount(): Int {
    return list.size
}

override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
   holder.bind(position)
}
}