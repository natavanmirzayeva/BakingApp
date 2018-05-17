package com.project.udacity.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.udacity.bakingapp.Ingredient;
import com.project.udacity.bakingapp.MainActivity;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Recipe;
import com.project.udacity.bakingapp.RecipeAdapter;
import com.project.udacity.bakingapp.data.RecipeResponse;
import com.project.udacity.bakingapp.data.RecipesApi;
import com.project.udacity.bakingapp.utils.Variables;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mehseti on 3.5.2018.
 */

public class RecipeFragment extends Fragment
{

    public RecipeFragment(){}
    @BindView(R.id.recipe_recycler)
    RecyclerView recipe;
    static OkHttpClient httpClient;
    static Observable<JsonArray> call;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe,container,false);
        ButterKnife.bind(this,view);
        /*List<String> myDataSet = new ArrayList<String>();
        myDataSet.add("Tiramisu");
        myDataSet.add("Cheesecake");*/
        if(savedInstanceState == null)
        {
            linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            gridLayoutManager = new GridLayoutManager(getContext(),3);
            parseJson();
        }
        return view;
    }

    public  void parseJson()
    {
        httpClient = new OkHttpClient.Builder().build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Variables.URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit =  builder.client(httpClient).build();
        RecipesApi moviesApi =  retrofit.create(RecipesApi.class);
        call = moviesApi.listRecipes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        call.map(new Function<JsonArray, List<Recipe>>() {
            @Override
            public List<Recipe> apply(JsonArray recipeResponse) throws Exception
            {
                Gson gson = new Gson();

                List<Recipe> recipes = new ArrayList<>();
                for(int i=0;i<recipeResponse.size();i++)
                {
                    Recipe recipe  = gson.fromJson(recipeResponse.get(i), Recipe.class);
                    recipes.add(recipe);
                }
                ;
               /* for(int i=0;i<recipeResponse.size();i++)
                {

                    JsonObject jsonObject = recipeResponse.get(i).getAsJsonObject();
                    int id = jsonObject.get("id").getAsInt();
                    String name = jsonObject.get("name").toString();
                    List<Ingredient> ingredients = new ArrayList<>();
                    jsonObject.get("ingredients").
                    for (int j=0;j<jsonObject.getAsJsonArray("ingredients").get(i).getAsJsonObject().size();j++)
                    {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setIngredient(jsonObject.getAsJsonArray("ingredients").get(j).getAsJsonObject().get("ingredient").toString());
                        Log.d("quantity", String.valueOf(ingredient.getIngredient()));
                        ingredients.add(ingredient);
                    }


                    recipe.setId(id);
                    recipe.setName(delete_quotes(name));
                    recipes.add(recipe);
                } */

                return recipes;
            }
        })
                .subscribe
                        (
                                new Consumer<List<Recipe>>() {
                                    @Override
                                    public void accept(List<Recipe> recipes) throws Exception
                                    {
                                        displayMovies(recipes);
                                    }
                                },
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.i("error", "RxJava2, HTTP Error: " + throwable.getMessage());
                                    }
                                }
                        );
    }

    void displayMovies(List<Recipe> recipeList)
    {
        RecipeAdapter recipeAdapter = new RecipeAdapter(recipeList, new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("Recipe",recipe);
                RecipeDetailFragment recipeFragment = new RecipeDetailFragment();
                recipeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragmet_container,recipeFragment)
                        .commit();
            }
        });

        recipe.setAdapter(recipeAdapter);
        if(isTablet(getContext())) recipe.setLayoutManager(gridLayoutManager);
        else recipe.setLayoutManager(linearLayoutManager);

    }

    static String delete_quotes(String word)
    {
        return word.replaceAll("^\"|\"$" ,"");
    }

    public static boolean isTablet(Context ctx){
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
