package com.project.udacity.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mehseti on 12.5.2018.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder>
{
    List<Step> steps;
    static TextView stepDescription;
    //static CardView cardView;


    public interface OnItemClickListener {
        void onItemClick(Step step);
    }
    private RecipeStepAdapter.OnItemClickListener listener;

    public RecipeStepAdapter(@NonNull List<Step> objects, RecipeStepAdapter.OnItemClickListener listener)
    {
        steps = objects;
        this.listener = listener;
    }

public static class RecipeStepViewHolder extends RecyclerView.ViewHolder
{
    public RecipeStepViewHolder(View v, final Context context)
    {
        super(v);
        stepDescription =  v.findViewById(R.id.txt);
        //cardView = v.findViewById(R.id.cardView);
    }

    public void bind(final Step step, final RecipeStepAdapter.OnItemClickListener listener)
    {
           /* cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(ingredient);
                }
            }); */
    }
}

    @Override
    public RecipeStepAdapter.RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeStepAdapter.RecipeStepViewHolder(view,parent.getContext());
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapter.RecipeStepViewHolder holder, int position)
    {
        holder.bind(steps.get(position), listener);
        Step step = steps.get(position);
        stepDescription.setText(step.getShortDescription());
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
