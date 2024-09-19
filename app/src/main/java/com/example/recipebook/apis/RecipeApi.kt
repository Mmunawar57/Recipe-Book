package com.example.recipebook.apis

import com.example.recipebook.modelclasses.RecipeList

import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.GET

interface RecipeApi {
    @GET("/api/recipes/v2")
    suspend fun getData(  // Make sure 'suspend' is included
        @Query("type") type: String = "public",
        @Query("q") recipeName: String,
        @Query("app_id") appId: String = "7fbbaae8",
        @Query("app_key") appKey: String = "90902800047f00d776881b93e555c3a3"
    ): Response<RecipeList>
}


