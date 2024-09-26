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
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        }

    }