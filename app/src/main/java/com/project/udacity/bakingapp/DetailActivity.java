package com.project.udacity.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.project.udacity.bakingapp.ui.RecipeDetailFragment;
import com.project.udacity.bakingapp.ui.RecipeDetailMediaFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    Intent intent;
    static RecipeDetailFragment recipeDetailFragment;
    RecipeDetailMediaFragment recipeDetailMediaFragment;

    @BindView(R.id.fragment_recipe_detail_media)
    FrameLayout fragmentRecipeDetailMedia;

    @BindView(R.id.fragment_recipe_detail)
    FrameLayout fragmentRecipeDetail;

    @BindView(R.id.lineVertical)
    View lineVertical;

    static boolean isVideoStarted = false;
    boolean twoPane;
    boolean isConnWarning = false;

    public Recipe getRecipe() {
        return recipe;
    }

    Recipe recipe;

    RecipeDetailMediaFragment currentMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        intent = getIntent();

        recipe = intent.getParcelableExtra("Recipe");


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipe Details");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


        recipeDetailFragment = new RecipeDetailFragment();
        recipeDetailMediaFragment = new RecipeDetailMediaFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", recipe);
        recipeDetailFragment.setArguments(bundle);

        twoPane = getResources().getBoolean(R.bool.isTablet) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;


        if (twoPane) {
            recipeDetailFragment.setArguments(bundle);

            fragmentRecipeDetailMedia.setVisibility(View.VISIBLE);
            fragmentRecipeDetail.setVisibility(View.VISIBLE);
            lineVertical.setVisibility(View.VISIBLE);
            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        //.remove(recipeFragment1)
                        .replace(R.id.fragment_recipe_detail, recipeDetailFragment, "detail")
                        //.addToBackStack(null)
                        .commit();

                fragmentManager.beginTransaction()
                        //.remove(recipeFragment1)
                        .replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment, "media")
                        //.addToBackStack(null)
                        .commit();
                recipeDetailMediaFragment.setStep(null);
            }


        } else if (isVideoStarted) {

            fragmentRecipeDetailMedia.setVisibility(View.VISIBLE);
            fragmentRecipeDetail.setVisibility(View.GONE);
            lineVertical.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        //.remove(recipeFragment1)
                        .replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment, "media")
                        //.addToBackStack(null)
                        .commit();
            }
        } else {

            fragmentRecipeDetailMedia.setVisibility(View.GONE);
            fragmentRecipeDetail.setVisibility(View.VISIBLE);
            lineVertical.setVisibility(View.GONE);
            if (savedInstanceState == null) {
                recipeDetailFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                fragmentTransaction1
                        .replace(R.id.fragment_recipe_detail, recipeDetailFragment, "detail");

                fragmentTransaction1.commit();
            }
        }
    }


    public static void setVideoStarted(boolean started) {
        DetailActivity.isVideoStarted = started;
    }


    @Override
    public void onBackPressed() {

        getSupportActionBar().show();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Show Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }

        if (isVideoStarted && !twoPane) {
            fragmentRecipeDetail.setVisibility(View.VISIBLE);
            fragmentRecipeDetailMedia.setVisibility(View.GONE);
            lineVertical.setVisibility(View.GONE);


        } else {
            super.onBackPressed();
        }
        setVideoStarted(false);

        if (currentMediaPlayer != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
            fragmentTransaction1.remove(currentMediaPlayer);
            fragmentTransaction1.commit();
            currentMediaPlayer.releasePlayer();
            currentMediaPlayer = null;
        }
    }


    public void setCurrentMediaPlayer(RecipeDetailMediaFragment currentMediaPlayer) {
        this.currentMediaPlayer = currentMediaPlayer;
    }

    public void goStep(int no) {
        recipeDetailFragment.goStep(no);
    }

    public boolean isConnWarning() {
        return isConnWarning;
    }

    public void setConnWarning(boolean connWarning) {
        isConnWarning = connWarning;
    }

    public void setMedia(RecipeDetailMediaFragment recipeDetailMediaFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                //.remove(recipeFragment1)
                .replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment, "media")
                //.addToBackStack(null)
                .commit();


    }

}
