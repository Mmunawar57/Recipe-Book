package com.example.recipebook

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.adapters.CategoriesAdapter
import com.example.recipebook.adapters.RecipeListAdapter
import com.example.recipebook.databinding.ActivityMainBinding
import com.example.recipebook.databinding.FragmentMainBinding
import com.example.recipebook.livedata.RecipeListModel
import com.example.recipebook.modelclasses.CategoresList
import com.example.recipebook.modelclasses.RecipeItemList
import com.example.recipebook.purchases.InAppPurchaseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Main : Fragment() {
    companion object {
        const val RETROFIT_DATA = "RETROFIT_DATA"
    }
    private lateinit var inAppPurchaseHandler: InAppPurchaseHandler
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
    private lateinit var viewmodel: RecipeListModel
    private val adapterListItem = mutableListOf<RecipeItemList>()
    private lateinit var binding: FragmentMainBinding
    private  val skus= listOf("weekly_subscription","monthly_subscription","yearly_subscription")
    override fun onStart() {
        super.onStart()
        inAppPurchaseHandler.startConnection()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        inAppPurchaseHandler=InAppPurchaseHandler(requireActivity(), skus)

        viewmodel= ViewModelProvider(requireActivity())[RecipeListModel::class.java]
        lifecycleScope.launch(Dispatchers.IO) {
            if(isInternetAvailable(requireActivity())) {
                withContext(Dispatchers.Main) {
                    viewmodel.recipeListLiveData.observe(requireActivity()) { list ->
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
                        val adapter = RecipeListAdapter(requireContext(), adapterListItem) {recipe->
                            Toast.makeText(requireContext(), recipe.recipeTitle, Toast.LENGTH_SHORT).show()
                            Log.d("RECIPE_LIST", "ONITEMCLICKED list-->$recipe")

                        }
                        binding.recipeList.adapter = adapter
                    }
                }
                viewmodel.getRecipeList("chicken")
            }else{
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        if(isInternetAvailable(requireContext())) {
        adapter = CategoriesAdapter(categories) { postion ->
                adapterListItem.clear()
                lifecycleScope.launch(Dispatchers.Main) {
                    viewmodel.recipeListLiveData.observe(requireActivity()) { list ->
                        for (i in list) {
                            for (j in i.hits) {
                                val calories = String.format("%.0f", j.recipe.calories)
                                j.recipe.ingredients[0].text
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
                            RecipeListAdapter(requireContext(), adapterListItem) { recipe ->
                            //    Log.d("RECIPE_LIST", "ONITEMCLICKED list-->$recipe")
                                Toast.makeText(requireContext(), recipe.recipeTitle, Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_main_to_recipeDetailsFragment)


                            }
                        binding.recipeList.hasFixedSize()
                        binding.recipeList.adapter = adapter

                    }
                    viewmodel.getRecipeList(categories[postion].title)
                }
            }
            binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewCategories.hasFixedSize()
            binding.recyclerViewCategories.adapter = adapter
        }


        binding.imgSearch.setOnClickListener {
            inAppPurchaseHandler.showConfirmationDialog(skus[0])
         /*   lifecycleScope.launch {
                recipeName = binding.editTextFindRecipe.text.toString()
                if(binding.editTextFindRecipe.text.isNotEmpty()) {
                    adapterListItem.clear()
                    viewmodel.recipeListLiveData.observe(requireActivity()) { list ->
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

                        val adapter = RecipeListAdapter(requireContext(), adapterListItem) {recipe->
                            Toast.makeText(requireContext(), recipe.recipeTitle, Toast.LENGTH_SHORT).show()

                        }
                        binding.recipeList.hasFixedSize()
                        binding.recipeList.adapter = adapter
                    }

                }
                viewmodel.getRecipeList(recipeName)
            }*/
        }
        return binding.root
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