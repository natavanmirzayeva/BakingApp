package com.project.udacity.bakingapp.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.udacity.bakingapp.DetailActivity;
import com.project.udacity.bakingapp.Ingredient;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Recipe;
import com.project.udacity.bakingapp.RecipeDetailAdapter;
import com.project.udacity.bakingapp.RecipeStepAdapter;
import com.project.udacity.bakingapp.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mehseti on 11.5.2018.
 */

public class RecipeDetailFragment extends Fragment {
    public RecipeDetailFragment() {
    }

    @BindView(R.id.recipe_detail_recycler)
    RecyclerView steps_recycler;

    @BindView(R.id.ingredients_list)
    RecyclerView ingredients_recycler;

    static Recipe recipe;
    LinearLayoutManager linearLayoutManager, linearLayoutManager_steps;

    @SuppressLint("StaticFieldLeak")
    static DetailActivity activity = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);
        activity = (DetailActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Recipe Details");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
            TextView textView = toolbar.findViewById(R.id.txt_toolbar);
            textView.setText("Recipe Details");
            toolbar.setNavigationIcon(R.drawable.ic_android_navigation_black_24dp);
        }

        recipe = getArguments().getParcelable("recipe");
        RecipeDetailAdapter recipeDetailAdapter = new RecipeDetailAdapter(recipe.getIngredients(), new RecipeDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingredient ingredient) {

            }
        });
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        ingredients_recycler.setAdapter(recipeDetailAdapter);
        ingredients_recycler.setLayoutManager(linearLayoutManager);
        RecipeStepAdapter recipeStepAdapter = new RecipeStepAdapter(recipe.getSteps(), new RecipeStepAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Step step) {
                activity.setVideoStarted(true);

                boolean twoPane = getResources().getBoolean(R.bool.isTablet) && getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

                goStep(step.getId());

                if (!twoPane) {
                    activity.findViewById(R.id.fragment_recipe_detail).setVisibility(View.GONE);
                    activity.findViewById(R.id.fragment_recipe_detail_media).setVisibility(View.VISIBLE);

                }

            }
        });

        linearLayoutManager_steps = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        steps_recycler.setAdapter(recipeStepAdapter);
        steps_recycler.setLayoutManager(linearLayoutManager_steps);
        return view;
    }


    public void goStep(int no) {

        if (no >= 0 && no < recipe.getSteps().size()) {



            Step step = recipe.getSteps().get(no);
            Bundle bundle = new Bundle();
            bundle.putParcelable("Step", step);
            RecipeDetailMediaFragment recipeDetailMediaFragment = new RecipeDetailMediaFragment();
            recipeDetailMediaFragment.setArguments(bundle);


            activity.setCurrentMediaPlayer(recipeDetailMediaFragment);
            boolean isTablet = activity.getResources().getBoolean(R.bool.isTablet);
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
            activity.setMedia(recipeDetailMediaFragment);

        }
    }
}
