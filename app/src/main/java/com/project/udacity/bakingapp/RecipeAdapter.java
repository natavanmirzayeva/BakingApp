package com.project.udacity.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mehseti on 3.5.2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>
{
    List<Recipe> recipes;
    static TextView recipe,servings,steps;
    static CardView cardView;


    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }
    private OnItemClickListener listener;

    public RecipeAdapter(@NonNull List<Recipe> objects,OnItemClickListener listener)
    {
        recipes = objects;
        this.listener = listener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder
    {
        public RecipeViewHolder(View v, final Context context)
        {
            super(v);
            recipe =  v.findViewById(R.id.txt);
            servings = v.findViewById(R.id.servings);
            steps = v.findViewById(R.id.steps);
            cardView = v.findViewById(R.id.cardView);
        }

        public void bind(final Recipe recipe, final OnItemClickListener listener)
        {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(recipe);
                }
            });
        }
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view,parent.getContext());
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position)
    {
        holder.bind(recipes.get(position), listener);
        Recipe recipeStr = recipes.get(position);
        recipe.setText(recipeStr.getName());
        servings.setText(String.valueOf(recipeStr.getServings()));
        steps.setText(String.valueOf(recipeStr.getSteps().size()));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
