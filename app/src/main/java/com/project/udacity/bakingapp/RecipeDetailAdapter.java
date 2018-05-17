package com.project.udacity.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.udacity.bakingapp.ui.RecipeDetailFragment;

import java.util.List;

/**
 * Created by mehseti on 11.5.2018.
 */

public class RecipeDetailAdapter extends  RecyclerView.Adapter<RecipeDetailAdapter.RecipeDetailViewHolder>
{
    List<Ingredient> ingredients;
    static TextView quantity,measure,ingredient;
    //static CardView cardView;


    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }
    private RecipeDetailAdapter.OnItemClickListener listener;

    public RecipeDetailAdapter(@NonNull List<Ingredient> objects, RecipeDetailAdapter.OnItemClickListener listener)
    {
        ingredients = objects;
        this.listener = listener;
    }

    public static class RecipeDetailViewHolder extends RecyclerView.ViewHolder
    {
        public RecipeDetailViewHolder(View v, final Context context)
        {
            super(v);
            quantity =  v.findViewById(R.id.txt_quantity);
            measure =  v.findViewById(R.id.txt_measure);
            ingredient =  v.findViewById(R.id.txt_ingredient);
            //cardView = v.findViewById(R.id.cardView);
        }

        public void bind(final Ingredient ingredient, final RecipeDetailAdapter.OnItemClickListener listener)
        {
           /* cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(ingredient);
                }
            }); */
        }
    }

    @Override
    public RecipeDetailAdapter.RecipeDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new RecipeDetailAdapter.RecipeDetailViewHolder(view,parent.getContext());
    }

    @Override
    public void onBindViewHolder(RecipeDetailAdapter.RecipeDetailViewHolder holder, int position)
    {
        holder.bind(ingredients.get(position), listener);
        Ingredient ingredient = ingredients.get(position);
        quantity.setText(String.valueOf(ingredient.getQuantity()));
        measure.setText(ingredient.getMeasure());
        this.ingredient.setText(ingredient.getIngredient());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}

