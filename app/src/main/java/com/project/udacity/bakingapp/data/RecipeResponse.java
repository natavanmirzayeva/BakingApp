package com.project.udacity.bakingapp.data;

import com.google.gson.annotations.SerializedName;
import com.project.udacity.bakingapp.Recipe;

import java.util.List;

/**
 * Created by mehseti on 6.5.2018.
 */

public class RecipeResponse
{
    @SerializedName("")
   public List<Recipe> recipes;

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
