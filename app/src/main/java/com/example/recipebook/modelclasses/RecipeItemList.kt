package com.example.recipebook.modelclasses

data class RecipeItemList(
    val recipeImage: String,
    val recipeTitle: String,
    val calories: String,
    val ingredients: String,
    val recipe: Recipe
) {
}