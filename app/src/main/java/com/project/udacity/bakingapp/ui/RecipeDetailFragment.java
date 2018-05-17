package com.project.udacity.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.project.udacity.bakingapp.Ingredient;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Recipe;
import com.project.udacity.bakingapp.RecipeDetailAdapter;
import com.project.udacity.bakingapp.RecipeStepAdapter;
import com.project.udacity.bakingapp.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

/**
 * Created by mehseti on 11.5.2018.
 */

public class RecipeDetailFragment extends Fragment
{
    public RecipeDetailFragment(){}
    @BindView(R.id.ingredients_list)
    RecyclerView ingredients_recycler;
    @BindView(R.id.recipe_detail_recycler)
    RecyclerView steps_recycler;
    static OkHttpClient httpClient;
    static Observable<JsonArray> call;
    LinearLayoutManager linearLayoutManager,linearLayoutManager_steps;
    GridLayoutManager gridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_detail,container,false);
        ButterKnife.bind(this,view);
        Recipe recipe = getArguments().getParcelable("Recipe");
        RecipeDetailAdapter recipeDetailAdapter = new RecipeDetailAdapter(recipe.getIngredients(), new RecipeDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingredient ingredient) {

            }
        });
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        ingredients_recycler.setAdapter(recipeDetailAdapter);
        ingredients_recycler.setLayoutManager(linearLayoutManager);
        RecipeStepAdapter recipeStepAdapter = new RecipeStepAdapter(recipe.getSteps(), new RecipeStepAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Step step) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("Step",step);
                RecipeDetailFragment recipeFragment = new RecipeDetailFragment();
                recipeFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.fragmet_container,recipeFragment)
                        .commit();
            }
        });
        linearLayoutManager_steps = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        steps_recycler.setAdapter(recipeStepAdapter);
        steps_recycler.setLayoutManager(linearLayoutManager_steps);
        /*List<String> myDataSet = new ArrayList<String>();
        myDataSet.add("Tiramisu");
        myDataSet.add("Cheesecake");*/
        if(savedInstanceState == null)
        {

            gridLayoutManager = new GridLayoutManager(getContext(),3);
            //parseJson();
        }
        return view;
    }
}
