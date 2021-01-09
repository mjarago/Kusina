package com.markarago.kusina.viewmodels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.markarago.kusina.data.Repository
import com.markarago.kusina.data.database.RecipesEntity
import com.markarago.kusina.models.FoodRecipe
import com.markarago.kusina.util.Constants
import com.markarago.kusina.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception



class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    /** ROOM DATABASE **/

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()

    /**
     * @param recipesEntity
     */
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }


    /**
     * RETROFIT
     */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    /**
     * @param queries - Set of queries required and/or additional parameters for the get request
     * @see getRecipesSafeCall
     */
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    /**
     * @
     * @param queries - Set of queries required and/or additional parameters for the get request
     */
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        if(hasInternetConnection()) {
            // make get request
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                // cache data
                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }

            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error(Constants.RECIPES_NOT_FOUND)
            }
        } else {
            recipesResponse.value = NetworkResult.Error(Constants.NO_INTERNET_CONNECTION)
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(Constants.NETWORK_TIMEOUT)
            }
            response.code() == 402 -> {
                return NetworkResult.Error(Constants.API_LIMITED)
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(Constants.RECIPES_NOT_FOUND)
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return  NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    /**
     * @return  whether the user has an internet connection or not
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

    }
}