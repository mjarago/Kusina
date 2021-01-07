package com.markarago.kusina.api

import com.markarago.kusina.BuildConfig
import com.markarago.kusina.models.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipesApi {

    companion object {
        const val BASE_URL = "https://api.spoonacular.com"
    }

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>
}