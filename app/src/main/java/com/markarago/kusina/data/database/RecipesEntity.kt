package com.markarago.kusina.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.markarago.kusina.models.FoodRecipe
import com.markarago.kusina.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipe) {

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

}