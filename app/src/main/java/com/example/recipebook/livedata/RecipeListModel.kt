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

class RecipeListModel(application: Application) : AndroidViewModel(application) {
    private val _recipeListItems = MutableLiveData<List<RecipeList>>()
    val recipeListLiveData: LiveData<List<RecipeList>> get() = _recipeListItems
    private lateinit var recipeDetails:RecipeList
    private var recipeApi: RecipeApi = RetrofitObj.getInstance().create(RecipeApi::class.java)

    suspend fun getRecipeList(title: String) {

        GlobalScope.launch(Dispatchers.Main) {
            val response = recipeApi.getData("public", title)
            if(response.isSuccessful) {
                val body = response.body()
                val recipeItems = mutableListOf<RecipeItemList>()

                body?.apply {
                    recipeDetails=RecipeList(_links, count, from, hits, to)
                }
                for (i in body!!.hits) {
                    val calories = String.format("%.0f", i.recipe.calories)
                        recipeItems.add(RecipeItemList(
                            (i.recipe.image),
                            i.recipe.label,
                            "$calories Calories",
                            "${i.recipe.ingredients.size} Intergents"
                        )
                    )
                }
                _recipeListItems.value = listOf(recipeDetails)
            } else {
                Log.e("RETROFIT_DATA", "Failed to fetch data: ${response.code()}")
            }
        }
    }
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
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