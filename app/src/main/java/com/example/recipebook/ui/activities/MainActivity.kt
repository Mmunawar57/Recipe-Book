package com.example.recipebook.ui.activities

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.adapters.CategoriesAdapter
import com.example.recipebook.adapters.RecipeListAdapter
import com.example.recipebook.apis.RecipeApi
import com.example.recipebook.apis.RetrofitObj
import com.example.recipebook.databinding.ActivityMainBinding
import com.example.recipebook.livedata.RecipeListModel
import com.example.recipebook.modelclasses.CategoresList
import com.example.recipebook.modelclasses.RecipeItemList
import com.example.recipebook.modelclasses.RecipeList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
class MainActivity : AppCompatActivity() {
    companion object {
        const val RETROFIT_DATA = "RETROFIT_DATA"
    }
    private lateinit var adapter: CategoriesAdapter
    private val categories = listOf(
                CategoresList("Chicken"),
                CategoresList("Pasta"),
                CategoresList("Mutton"),
                CategoresList("Rice"),
                CategoresList("Sushi"),
                CategoresList("Salad"),
                CategoresList("Burger"),
                CategoresList("Pizza")
    )
    private var recipeName = "chicken"
    private lateinit var viewmodel:RecipeListModel
    private val adapterListItem = mutableListOf<RecipeItemList>()
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        setContentView(binding.root)

        viewmodel=ViewModelProvider(this@MainActivity)[RecipeListModel::class.java]
       lifecycleScope.launch(Dispatchers.IO) {
            if(isInternetAvailable(this@MainActivity)) {
                withContext(Dispatchers.Main) {
                    viewmodel.recipeListLiveData.observe(this@MainActivity) { list ->
                        adapterListItem.clear()
                        for (i in list){
                            for (j in i.hits) {
                                val calories = String.format("%.0f", j.recipe.calories)
                                adapterListItem.add(RecipeItemList(
                                    (j.recipe.image),
                                    j.recipe.label,
                                    "$calories Calories",
                                    "${j.recipe.ingredients.size} Intergents"
                                )
                                )
                                Log.d("RECIPE_LIST", "ONITEMCLICKED Recipe-->${j.recipe.ingredients[0].text}")

                            }
                            //   adapterListItem.addAll(RecipeItemList(i._links.))
                        }
                        val adapter = RecipeListAdapter(this@MainActivity, adapterListItem) {recipe->
                            Toast.makeText(this@MainActivity, recipe.recipeTitle, Toast.LENGTH_SHORT).show()
                            Log.d("RECIPE_LIST", "ONITEMCLICKED list-->$recipe")

                        }
                        binding.recipeList.adapter = adapter
                    }
                }
                viewmodel.getRecipeList("chicken")
            }else{
                Toast.makeText(this@MainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
            }

        adapter = CategoriesAdapter(categories) { postion ->
            if(isInternetAvailable(this@MainActivity)) {
                adapterListItem.clear()
                lifecycleScope.launch(Dispatchers.Main) {
                    viewmodel.recipeListLiveData.observe(this@MainActivity) { list ->
                        for (i in list) {
                            for (j in i.hits) {
                                val calories = String.format("%.0f", j.recipe.calories)
                                adapterListItem.add(
                                    RecipeItemList(
                                        (j.recipe.image),
                                        j.recipe.label,
                                        "$calories Calories",
                                        "${j.recipe.ingredients.size} Intergents"
                                    )
                                )
                                Log.d(
                                    "RECIPE_LIST",
                                    "ONITEMCLICKED Recipe-->${j.recipe.ingredients[0].text}"
                                )

                            }
                            //   adapterListItem.addAll(RecipeItemList(i._links.))
                        }
                        val adapter =
                            RecipeListAdapter(this@MainActivity, adapterListItem) { recipe ->
                                Log.d("RECIPE_LIST", "ONITEMCLICKED list-->$recipe")
                                Toast.makeText(this@MainActivity, recipe.recipeTitle, Toast.LENGTH_SHORT).show()


                            }
                        binding.recipeList.hasFixedSize()
                        binding.recipeList.adapter = adapter

                    }
                    viewmodel.getRecipeList(categories[postion].title)
                }
            }else{
                Toast.makeText(this@MainActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.hasFixedSize()
        binding.recyclerViewCategories.adapter = adapter

        binding.imgSearch.setOnClickListener {
            lifecycleScope.launch {
                recipeName = binding.editTextFindRecipe.text.toString()
                if(binding.editTextFindRecipe.text.isNotEmpty()) {
                    adapterListItem.clear()
                        viewmodel.recipeListLiveData.observe(this@MainActivity) { list ->
                            for (i in list){
                                for (j in i.hits) {
                                    val calories = String.format("%.0f", j.recipe.calories)
                                    adapterListItem.add(RecipeItemList(
                                        (j.recipe.image),
                                        j.recipe.label,
                                        "$calories Calories",
                                        "${j.recipe.ingredients.size} Intergents"
                                    )
                                    )
                                }
                             //   adapterListItem.addAll(RecipeItemList(i._links.))
                            }

                            val adapter = RecipeListAdapter(this@MainActivity, adapterListItem) {recipe->
                                Toast.makeText(this@MainActivity, recipe.recipeTitle, Toast.LENGTH_SHORT).show()

                            }
                            binding.recipeList.hasFixedSize()
                            binding.recipeList.adapter = adapter
                        }

                }
                viewmodel.getRecipeList(recipeName)
            }
            }
        }



    private fun isInternetAvailable(context: Context): Boolean {
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