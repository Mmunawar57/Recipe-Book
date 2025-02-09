package com.example.recipebook

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.recipebook.adapters.NutritionAdapter
import com.example.recipebook.databinding.FragmentRecipeDetailsBinding
import com.example.recipebook.livedata.RecipeListModel
import com.example.recipebook.modelclasses.adapterModels.Nutrition


class RecipeDetailsFragment : Fragment() {
    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var viewModel: RecipeListModel
    private lateinit var context: Context
    private  var list=mutableListOf<Nutrition>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        context=requireContext()
        binding=FragmentRecipeDetailsBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(requireActivity())[RecipeListModel::class.java]


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.recipeSelectedItemLiveData.observe(requireActivity()) {list->
            Log.d("RECIPE_DETAILS", "all list-->:${list} ")
            Log.d("RECIPE_DETAILS", "nutritions list--:${list.recipe.totalNutrients} ")
            Glide.with(context).clear(binding.imgTitle)
            Glide.with(context).load(list.recipeImage).into(binding.imgTitle)
            binding.txtRecipeName.text = list.recipeTitle
            binding.txtMealValue.text=list.recipe.mealType.joinToString(", ")
            binding.txtCousineValue.text = list.recipe.cuisineType.joinToString(", ")
            val healthLabels = list.recipe.healthLabels.first().toString()
            val dishType = list.recipe.dishType.joinToString(", ")
            binding.txtCookTimeValue.text=healthLabels
            binding.txtPrepTimeValue.text=dishType
            Log.d("RECIPE_DETAILS_123", "nutritions dish type--:$dishType ")
            Log.d("RECIPE_DETAILS_123", "nutritions health--:${healthLabels.length} ")

            binding.txtRecipeDescription.text=list.recipe.ingredientLines.joinToString(separator = "\n")
            // Map the digest data to Nutrition objects
            val nutritionList = list.recipe.digest.map {
                val formattedTotal = String.format("%.4f", it.total).toDouble()
                Nutrition(
                    nutritionTitle = it.label,
                    nutritionValue = "$formattedTotal ${it.unit}"
                )
            }

            // Set the adapter
            setAdapter(nutritionList)
        }
        super.onViewCreated(view, savedInstanceState)

    }
    private fun setAdapter(list:List<Nutrition>){
        val adapter= NutritionAdapter(list)
        binding.recyclerView.layoutManager= LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter=adapter

    }

}