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
import com.example.recipebook.adapters.MultipleViewAdapter
import com.example.recipebook.modelclasses.adapterModels.TypeA
import com.example.recipebook.modelclasses.adapterModels.TypeB

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val list_A = listOf(
        TypeA(R.drawable.ic_launcher_background, "name1", "desc1"),
        TypeA(R.drawable.ic_launcher_background, "name2", "desc2"),
        TypeA(R.drawable.ic_launcher_background, "name3", "desc3"),
        TypeA(R.drawable.ic_launcher_background, "name4", "desc4"),
        TypeA(R.drawable.ic_launcher_background, "name5", "desc5"),
        TypeA(R.drawable.ic_launcher_background, "name6", "desc6"),
        TypeA(R.drawable.ic_launcher_background, "name7", "desc7"),
        TypeA(R.drawable.ic_launcher_background, "name8", "desc8"),
        TypeA(R.drawable.ic_launcher_background, "name9", "desc9"),
        TypeA(R.drawable.ic_launcher_background, "name10", "desc10"),
        TypeA(R.drawable.ic_launcher_background, "name11", "desc11"),
        TypeA(R.drawable.ic_launcher_background, "name12", "desc12"),
        TypeA(R.drawable.ic_launcher_background, "name13", "desc13"),
    )
    private val list_B = listOf(
        TypeB(R.drawable.splash),
        TypeB(R.drawable.splash),
        TypeB(R.drawable.splash),
        TypeB(R.drawable.splash)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        setAdapter(list_A)
    }

    private fun setAdapter(list: List<TypeA>) {
        val combiendList = mutableListOf<Any>()
        for((index,item) in list.withIndex()){
            combiendList.add(item)
            if((index+1)%5==0){
                combiendList.add(list_B)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MultipleViewAdapter(combiendList) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    }
}