package com.project.udacity.bakingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.project.udacity.bakingapp.MainActivity;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Recipe;
import com.project.udacity.bakingapp.RecipeAdapter;
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
    private static final String RECYCLERVIEW_STATE_ADAPTER = "recyclerview-state-adapter";
    RecipeAdapter recipeAdapter;
    Parcelable savedRecyclerLayoutState;
    private static final String RECYCLERVIEW_STATE = "recyclerview-state-1";
    private boolean mTwoPane;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe,container,false);
        ButterKnife.bind(this,view);
        setRetainInstance(true);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.txt_toolbar);
        textView.setText("Recipes");
        final MainActivity activity = (MainActivity) getActivity();

        if (toolbar != null)
        {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Recipes");
            if(toolbar.getNavigationIcon() != null) toolbar.setNavigationIcon(null);
        }


            linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            gridLayoutManager = new GridLayoutManager(getContext(),3);
            parseJson();
        
        if(isTablet(getContext())) recipe.setLayoutManager(gridLayoutManager);
        else recipe.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
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
                };
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
         recipeAdapter = new RecipeAdapter(recipeList, new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe)
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("Recipe",recipe);
                RecipeDetailFragment recipeFragment = new RecipeDetailFragment();
                recipeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction
                        //.remove(recipeFragment1)
                       // .replace(R.id.fragmet_container,recipeFragment,"detail")
                        .addToBackStack(null)
                        .commit();
            }
        });

        recipe.setAdapter(recipeAdapter);
    }

    public static boolean isTablet(Context ctx){
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLERVIEW_STATE, recipe.getLayoutManager().onSaveInstanceState());
        if(recipeAdapter!= null)
        {
            List<Recipe> movie = recipeAdapter.getRecipes();
            if (movie != null && !movie.isEmpty()) {
                outState.putParcelableArrayList(RECYCLERVIEW_STATE_ADAPTER, (ArrayList<? extends Parcelable>) movie);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
        {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLERVIEW_STATE);
            if (savedInstanceState.containsKey(RECYCLERVIEW_STATE_ADAPTER)) {
                List<Recipe> movieResultList = savedInstanceState.getParcelableArrayList(RECYCLERVIEW_STATE_ADAPTER);
                recipeAdapter.setRecipes(movieResultList);
                recipe.setAdapter(recipeAdapter);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (savedRecyclerLayoutState != null)
                    {
                        if (savedInstanceState.containsKey(RECYCLERVIEW_STATE_ADAPTER)) {
                            recipe.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                            savedRecyclerLayoutState = null;
                        }
                    }
                }
            }, 3000);
        }
    }


}
