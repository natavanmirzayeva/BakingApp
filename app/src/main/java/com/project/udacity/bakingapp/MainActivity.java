package com.project.udacity.bakingapp;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.project.udacity.bakingapp.ui.RecipeFragment;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.collapse)CollapsingToolbarLayout collapse;
    private static final String FRAGMENT_STACK_KEY = "FRAGMENT_STACK_KEY";

    private Stack<StackEntry> fragmentsStack = new Stack<StackEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        collapse.setTitle("Recipes");
        this.setTitle("Recipes");
        setSupportActionBar(toolbar);
        if(toolbar.getNavigationIcon() != null) toolbar.setNavigationIcon(null);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recipes");
        RecipeFragment recipeFragment = new RecipeFragment();

       if(savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .add(R.id.fragmet_container,recipeFragment,"recipe")
                    .commit();
        }
        else
        {
            Serializable serializable = savedInstanceState.getSerializable(FRAGMENT_STACK_KEY);
            if (serializable != null) {
                @SuppressWarnings("unchecked")
                List<StackEntry> arrayList = (List<StackEntry>) serializable;
                fragmentsStack = new Stack<StackEntry>();
                fragmentsStack.addAll(arrayList);
            }

            if (fragmentsStack.size() > 1) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                for (int i = 0; i < fragmentsStack.size()-1; i++) {
                    String fragTag = fragmentsStack.get(i).getFragTag();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragTag);
                    fragmentTransaction.hide(fragment);
                }
                fragmentTransaction.commit();
            }
        }
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    Fragment lastFragment = getLastFragment();
                    if (lastFragment != null && lastFragment.isHidden()) {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.show(lastFragment);
                        fragmentTransaction.commit();
                    }
                }
            });
    }

    private Fragment getLastFragment() {
        if (fragmentsStack.isEmpty()) return null;
        String fragTag = fragmentsStack.peek().getFragTag();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragTag);
        return fragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAGMENT_STACK_KEY, fragmentsStack);
    }

    @Override
    public void onBackPressed() {
        if (!fragmentsStack.isEmpty()) {
            fragmentsStack.pop();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private static class StackEntry implements Serializable {
        private static final long serialVersionUID = -6162805540320628024L;

        private String fragTag = null;
        public StackEntry(String fragTag) {
            super();
            this.fragTag = fragTag;
        }
        public String getFragTag() {
            return fragTag;
        }
    }

   /* @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    } */
}
