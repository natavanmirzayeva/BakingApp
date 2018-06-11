package com.project.udacity.bakingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.project.udacity.bakingapp.Ingredient;
import com.project.udacity.bakingapp.MainActivity;
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
    @BindView(R.id.recipe_detail_recycler)
    RecyclerView steps_recycler;
    @BindView(R.id.ingredients_list)
    RecyclerView ingredients_recycler;
    private boolean mTwoPane;
    LinearLayoutManager linearLayoutManager,linearLayoutManager_steps;
    GridLayoutManager gridLayoutManager;
    RecipeDetailMediaFragment recipeDetailMediaFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recipe_detail,container,false);
        ButterKnife.bind(this,view);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView textView = toolbar.findViewById(R.id.txt_toolbar);
        textView.setText("Recipe Details");
        toolbar.setNavigationIcon(R.drawable.ic_android_navigation_black_24dp);
        setRetainInstance(true);

        recipeDetailMediaFragment = new RecipeDetailMediaFragment();
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        recipeDetailMediaFragment = (RecipeDetailMediaFragment) fragmentManager.findFragmentByTag("media");
        if(recipeDetailMediaFragment == null)
        {
            recipeDetailMediaFragment = new RecipeDetailMediaFragment();
        }
       /* else
        {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.remove(recipeDetailMediaFragment).commit();
            fragmentManager.executePendingTransactions();
        } */
       /* FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipeDetailMediaFragment recipeDetailMediaFragment1 = (RecipeDetailMediaFragment) fragmentManager.findFragmentByTag("media");;
        if(recipeDetailMediaFragment1 != null)
        {
            fragmentManager.beginTransaction().remove(recipeDetailMediaFragment1).commit();
            fragmentManager.executePendingTransactions();
        }
        else
        {
            recipeDetailMediaFragment1 = new RecipeDetailMediaFragment();
        } */

        if(view.findViewById(R.id.linear_layout) != null)
        {
            mTwoPane = true;


            fragmentTransaction
                        //.remove(recipeFragment1)
                        .replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment, "media")
                        .addToBackStack("recipe")
                        .commit();

                /*RecipeDetailMediaFragment recipeDetailMediaFragment = new RecipeDetailMediaFragment();
                fragmentTransaction
                        .replace(R.id.fragment_recipe_detail_media,recipeDetailMediaFragment,"media")
                        .addToBackStack("recipe")
                        .commit(); */
                //RecipeDetailMediaFragment  recipeDetailMediaFragment = new RecipeDetailMediaFragment();
            //fragmentManager.beginTransaction().replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment1, "media").addToBackStack("recipe").commit();

        }
        else if(view.findViewById(R.id.linear_layout) != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            Toast.makeText(getContext(),"PORTRATİ   ",Toast.LENGTH_LONG);
        }
        else
        {
            mTwoPane = false;
          /*  FragmentTransaction fragmentTransaction1 = getActivity().getSupportFragmentManager().beginTransaction();
            RecipeDetailMediaFragment recipeDetailMediaFragment = (RecipeDetailMediaFragment) getActivity().getSupportFragmentManager().findFragmentByTag("media");;
            if(recipeDetailMediaFragment != null) {
                fragmentTransaction1
                        .replace(R.id.fragmet_container, recipeDetailMediaFragment, "media")
                        .addToBackStack("recipe")
                        .commit();
            } */
            /*recipeDetailMediaFragment1 = (RecipeDetailMediaFragment) fragmentManager.findFragmentByTag("media");;
            if(recipeDetailMediaFragment1 != null)
            {
               // fragmentManager.beginTransaction().remove(recipeDetailMediaFragment1).commit();
                fragmentManager.beginTransaction().replace(R.id.fragmet_container, recipeDetailMediaFragment1, "media").addToBackStack("detail").commit();

            } */
            //RecipeDetailMediaFragment recipeDetailMediaFragment1 = (RecipeDetailMediaFragment) fragmentManager.findFragmentByTag("media");;
            recipeDetailMediaFragment = (RecipeDetailMediaFragment) fragmentManager.findFragmentByTag("media");
            if(recipeDetailMediaFragment != null)
            {
                fragmentTransaction
                        .replace(R.id.fragment_recipe_detail_media,recipeDetailMediaFragment,"media")
                        .addToBackStack("detail")
                        .commit();
            }

           // fragmentManager.beginTransaction().replace(R.id.fragmet_container, recipeDetailMediaFragment, "media").addToBackStack("detail").commit();



        }
        /*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && mTwoPane == true)
        {
            view.findViewById(R.id.fragment_recipe_detail).setVisibility(View.GONE);
            view.findViewById(R.id.view).setVisibility(View.GONE);
        } */
        final MainActivity activity = (MainActivity) getActivity();
        if (toolbar != null)
        {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Recipe Details");
            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activity.onBackPressed();
                }
            });
        }

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
                RecipeDetailMediaFragment recipeDetailMediaFragment = new RecipeDetailMediaFragment();
                recipeDetailMediaFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(mTwoPane == true) {
                    fragmentTransaction
                            //.remove(recipeFragment1)
                            .replace(R.id.fragment_recipe_detail_media, recipeDetailMediaFragment, "media")
                            .addToBackStack("recipe")
                            .commit();
                }
                else
                {
                    fragmentTransaction
                            //.remove(recipeFragment1)
                            .replace(R.id.fragmet_container,recipeDetailMediaFragment,"media")
                            .addToBackStack("detail")
                            .commit();
                }
            }
        });

        linearLayoutManager_steps = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        steps_recycler.setAdapter(recipeStepAdapter);
        steps_recycler.setLayoutManager(linearLayoutManager_steps);
        if(savedInstanceState == null)
        {


            gridLayoutManager = new GridLayoutManager(getContext(),3);
            //parseJson();
        }

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*if(mTwoPane == true)
        {
            RecipeDetailMediaFragment recipeDetailMediaFragment = (RecipeDetailMediaFragment) getActivity().getSupportFragmentManager().findFragmentByTag("media");;

           if(recipeDetailMediaFragment != null)
           {
             try
             {
                 FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                 fragmentTransaction
                         //.remove(recipeFragment1)
                         .remove(recipeDetailMediaFragment)
                         .commit();
             }
             catch (Exception e)
             {
                 e.printStackTrace();
             }
           }
        }*/
    }
}
