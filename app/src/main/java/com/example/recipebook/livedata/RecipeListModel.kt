package com.example.recipebook.livedata

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipebook.adapters.RecipeListAdapter
import com.example.recipebook.apis.RecipeApi
import com.example.recipebook.apis.RetrofitObj
import com.example.recipebook.modelclasses.RecipeItemList
import com.example.recipebook.modelclasses.RecipeList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class RecipeListModel(application: Application) : AndroidViewModel(application) {
    private val _recipeListItems = MutableLiveData<List<RecipeList>>()
    val recipeListLiveData: LiveData<List<RecipeList>> get() = _recipeListItems
    private val recipeSelectedItem=MutableLiveData<RecipeItemList>()
    val recipeSelectedItemLiveData:LiveData<RecipeItemList> get() = recipeSelectedItem
    private lateinit var recipeDetails:RecipeList
    private var recipeApi: RecipeApi = RetrofitObj.getInstance().create(RecipeApi::class.java)


    fun selectRecipe(recipe:RecipeItemList) {
        recipeSelectedItem.value= recipe
    }
    suspend fun getRecipeList(title: String) {
            GlobalScope.launch(Dispatchers.Main) {
                val response = recipeApi.getData("public", title)
                Log.d("RETROFIT_DATA", "getRecipeList:-->Response $response")
                try {
                    if(response.isSuccessful) {
                        val body = response.body()
                        Log.d("RETROFIT_DATA", "getRecipeList:-->Body $body")

                        val recipeItems = mutableListOf<RecipeItemList>()
                        body?.apply {
                            recipeDetails = RecipeList(_links, count, from, hits, to)
                        }
                        if (body?.hits.isNullOrEmpty()) {
                            // Handle no results found
                            Log.d("RETROFIT_DATA", "No recipes found for the search query.")
                            _recipeListItems.value = emptyList() // Or show a "No results found" UI message
                            return@launch
                        }
                        for (i in body!!.hits) {
                            val calories = i.recipe.calories.takeUnless { it.isNaN() || it.isInfinite() } ?: 0.0
                            val totalCO2Emissions = i.recipe.totalCO2Emissions.takeUnless { it.isNaN() || it.isInfinite() } ?: 0.0

                            //    val calories = String.format("%.0f", i.recipe.calories)
                            recipeItems.add(
                                RecipeItemList(
                                    (i.recipe.image),
                                    i.recipe.label,
                                    "${String.format("%.0f", calories)} Calories",
                                    "${i.recipe.ingredients.size} Ingredients",
                                    i.recipe
                                )
                            )
                        }
                        _recipeListItems.value = listOf(recipeDetails)
                    }else{
                        Log.d("RETROFIT_DATA", "response failed $")

                    }

                }catch (e:Exception) {
                    Log.d("RETROFIT_DATA", "getRecipeList:-->Exception $e")
                }

            }
    }

}
//   suspend fun fetchDataFromApi(title:String){
//       val adapterListItem= mutableListOf<RecipeItemList>()
//        try {
//            val response =
//                recipeApi.getData(type = "public", recipeName =title)
//            if(response.isSuccessful) {
//                val recipeList = response.body()
//                for (i in recipeList!!.hits) {
//                    val calories = String.format("%.0f", i.recipe.calories)
//
//                    adapterListItem.add(
//                        RecipeItemList(
//                            (i.recipe.image),
//                            i.recipe.label,
//                            "$calories Calories",
//                            "${i.recipe.ingredients.size} Intergents"
//                        )
//                    )
//                    recipeListItem.value=recipeList
//                    Log.d(RETROFIT_DATA, "Recipe Calories: ${i.recipe.calories}")
//
//                }
//                Log.d(RETROFIT_DATA, "Recipe Response List: ${recipeList.hits}")
//            } else {
//                Log.e(RETROFIT_DATA, "Failed to fetch data: ${response.code()}")
//            }
//        } catch (e: Exception) {
//            Log.e(RETROFIT_DATA, "Failed to fetch data: ${e.message}")
//        }
//    }
   // }
//}