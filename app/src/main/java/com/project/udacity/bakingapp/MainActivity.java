package com.project.udacity.bakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recipe_recycler)
    RecyclerView recipe;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    static OkHttpClient httpClient;
    static Observable<JsonArray> call;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipes");
            if (toolbar.getNavigationIcon() != null) toolbar.setNavigationIcon(null);
        }


        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager = new GridLayoutManager(this, 3);

        checkConnection();

        if (isTablet(this)) recipe.setLayoutManager(gridLayoutManager);
        else recipe.setLayoutManager(linearLayoutManager);
        ;
    }

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null) {
            parseJson();
        } else {
            openDialog();
        }


    }

    public void parseJson() {
        httpClient = new OkHttpClient.Builder().build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Variables.URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient).build();
        RecipesApi moviesApi = retrofit.create(RecipesApi.class);
        call = moviesApi.listRecipes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        call.map(new Function<JsonArray, List<Recipe>>() {
            @Override
            public List<Recipe> apply(JsonArray recipeResponse) throws Exception {
                Gson gson = new Gson();

                List<Recipe> recipes = new ArrayList<>();
                for (int i = 0; i < recipeResponse.size(); i++) {
                    Recipe recipe = gson.fromJson(recipeResponse.get(i), Recipe.class);
                    recipes.add(recipe);
                }
                ;
                return recipes;
            }
        })
                .subscribe
                        (
                                new Consumer<List<Recipe>>() {
                                    @Override
                                    public void accept(List<Recipe> recipes) throws Exception {
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

    public static boolean isTablet(Context ctx) {
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    void displayMovies(List<Recipe> recipeList) {
        recipeAdapter = new RecipeAdapter(recipeList, new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Recipe", recipe);
                startActivity(intent);
            }
        });

        recipe.setAdapter(recipeAdapter);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void openDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("No Internet Connection");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Try Again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        checkConnection();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
