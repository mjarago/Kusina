package com.markarago.kusina.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.markarago.kusina.data.DataStoreRepository
import com.markarago.kusina.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.markarago.kusina.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.markarago.kusina.util.Constants.Companion.DEFAULT_RECIPES_COUNT
import com.markarago.kusina.util.Constants.Companion.INTERNET_CONNECTION_RESTORED
import com.markarago.kusina.util.Constants.Companion.NO_INTERNET_CONNECTION
import com.markarago.kusina.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.markarago.kusina.util.Constants.Companion.QUERY_API_KEY
import com.markarago.kusina.util.Constants.Companion.QUERY_DIET
import com.markarago.kusina.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.markarago.kusina.util.Constants.Companion.QUERY_NUMBER
import com.markarago.kusina.util.Constants.Companion.QUERY_TYPE
import com.markarago.kusina.util.Constants.Companion.SPOONACULAR_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RecipesViewModel @ViewModelInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }


    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                mealType =  value.selectedMealType
                dietType = value.selectedDietType
            }
        }


        queries[QUERY_API_KEY] = SPOONACULAR_API_KEY
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_COUNT // number of recipes (1 - 100)
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun showNetworkStatus() {
        if(!networkStatus) {
            Toast.makeText(getApplication(), NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if(networkStatus) {
            if(backOnline) {
                Toast.makeText(getApplication(), INTERNET_CONNECTION_RESTORED, Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}