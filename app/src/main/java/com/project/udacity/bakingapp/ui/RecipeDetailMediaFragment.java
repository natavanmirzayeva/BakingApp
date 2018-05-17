package com.project.udacity.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.project.udacity.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

/**
 * Created by mehseti on 12.5.2018.
 */

public class RecipeDetailMediaFragment extends Fragment
{
    public RecipeDetailMediaFragment(){}
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
        View view = inflater.inflate(R.layout.fragment_recipe_detail_media,container,false);
        ButterKnife.bind(this,view);
        /*List<String> myDataSet = new ArrayList<String>();
        myDataSet.add("Tiramisu");
        myDataSet.add("Cheesecake");*/
        if(savedInstanceState == null)
        {
            linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            gridLayoutManager = new GridLayoutManager(getContext(),3);
          
        }
        return view;
    }
}
