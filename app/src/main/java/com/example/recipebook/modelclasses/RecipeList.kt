package com.example.recipebook.modelclasses

data class RecipeList(
    val _links: Links,
    val count: Int,
    val from: Int,
    val hits: List<Hit>,
    val to: Int
)