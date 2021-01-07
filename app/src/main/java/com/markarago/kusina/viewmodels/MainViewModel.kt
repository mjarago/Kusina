package com.markarago.kusina.viewmodels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.markarago.kusina.data.Repository
import com.markarago.kusina.models.FoodRecipe
import com.markarago.kusina.util.Constants
import com.markarago.kusina.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception



class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {


    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    // coroutine
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        if(hasInternetConnection()) {
            // make get request
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)
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