package com.markarago.kusina.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.markarago.kusina.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.markarago.kusina.util.Constants.Companion.QUERY_API_KEY
import com.markarago.kusina.util.Constants.Companion.QUERY_DIET
import com.markarago.kusina.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.markarago.kusina.util.Constants.Companion.QUERY_NUMBER
import com.markarago.kusina.util.Constants.Companion.QUERY_TYPE
import com.markarago.kusina.util.Constants.Companion.SPOONACULAR_API_KEY

class RecipesViewModel(application: Application): AndroidViewModel(application) {

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_API_KEY] = SPOONACULAR_API_KEY
        queries[QUERY_NUMBER] = "50" // number of recipes (1 - 100)
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

}