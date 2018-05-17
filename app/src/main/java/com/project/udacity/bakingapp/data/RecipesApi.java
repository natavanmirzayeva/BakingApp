package com.project.udacity.bakingapp.data;

import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.List;

import retrofit2.http.GET;

/**
 * Created by mehseti on 6.5.2018.
 */

public interface RecipesApi
{
    @GET("baking.json")
    public io.reactivex.Observable<JsonArray> listRecipes();
}
