package com.project.udacity.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.udacity.bakingapp.ui.RecipeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecipeFragment recipeFragment = new RecipeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                .add(R.id.fragmet_container,recipeFragment)
                .commit();
    }
}
