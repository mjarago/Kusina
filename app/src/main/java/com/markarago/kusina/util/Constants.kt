package com.markarago.kusina.util

import com.markarago.kusina.Spoonacular

class Constants {
    companion object {
        const val API_LIMITED = "API Key Limited."
        const val NO_INTERNET_CONNECTION = "No Internet Connection"
        const val NETWORK_TIMEOUT = "Network Timeout."
        const val RECIPES_NOT_FOUND = "Recipes not found."
        const val SPOONACULAR_API_KEY = Spoonacular.API_KEY
        // API Query Keys
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        // ROOM Database

        const val DATABASE_NAME = "recipes database"
        const val RECIPES_TABLE = "recipes_table"

    }
}