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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Main : Fragment() {
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
    private lateinit var viewmodel: RecipeListModel
    private val adapterListItem = mutableListOf<RecipeItemList>()
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater)
        viewmodel = ViewModelProvider(requireActivity())[RecipeListModel::class.java]
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if(isInternetAvailable(requireContext())) {
                viewmodel.getRecipeList(recipeName)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                        .show()
                    delay(1000)
                    binding.progressBar.visibility=View.GONE
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            //initial data list when app activity launched
            adapter = CategoriesAdapter(categories) {
                adapterListItem.clear()
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    if(isInternetAvailable(requireContext())) {
                        viewmodel.getRecipeList(categories[it].title)
                        withContext(Dispatchers.Main) {
                            if(adapterListItem.isNotEmpty()){
                                binding.progressBar.visibility=View.GONE
                                binding.recipeList.visibility=View.VISIBLE
                                recipeList.adapter =
                                RecipeListAdapter(requireContext(), adapterListItem) {
                                }
                            }

                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "No Internet Connection",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
            // horizontal scroll view for categories
            recyclerViewCategories.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerViewCategories.adapter = adapter
            viewmodel.recipeListLiveData.observe(requireActivity()) { liveData ->
                liveData.forEach {
                    val recipeList = it.hits
                    Log.d("LIVE_DATA_OBSERVER", "onViewCreated:$liveData ")
                    adapterListItem.clear()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recipeList.visibility = View.INVISIBLE
                    for (i in recipeList) {
                        adapterListItem.add(
                            RecipeItemList(
                                (i.recipe.image),
                                i.recipe.label,
                                "${i.recipe.calories}",
                                "${i.recipe.ingredients.size}",
                                i.recipe
                            )
                        )
                    }
                }
                if(adapterListItem.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    binding.recipeList.visibility = View.VISIBLE
                    recipeList.adapter =
                        RecipeListAdapter(requireContext(), adapterListItem) { pos ->
                            viewmodel.selectRecipe(adapterListItem[pos])
                            findNavController().navigate(R.id.action_main_to_recipeDetailsFragment)
                        }
                }
            }
            imgSearch.setOnClickListener {
                if(isInternetAvailable(requireContext())){
                if(binding.editTextFindRecipe.text.toString().isNotEmpty()) {
                    recipeName = binding.editTextFindRecipe.text.toString()
                    adapterListItem.clear()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recipeList.visibility = View.INVISIBLE
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        viewmodel.getRecipeList(recipeName)
                        withContext(Dispatchers.Main) {
                            if(adapterListItem.isNotEmpty()){
                                binding.progressBar.visibility=View.GONE
                                binding.recipeList.visibility=View.VISIBLE
                                recipeList.adapter =
                                    RecipeListAdapter(requireContext(), adapterListItem) {}
                            }else{
                                delay(1000)
                                binding.progressBar.visibility=View.GONE
                            }
                        }
                    }
                    //   binding.editTextFindRecipe.text.clear()
                } else {
                    Toast.makeText(requireContext(), "Enter Recipe Name", Toast.LENGTH_SHORT).show()
                }
                    }else{
                    Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if(capabilities != null) {
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}