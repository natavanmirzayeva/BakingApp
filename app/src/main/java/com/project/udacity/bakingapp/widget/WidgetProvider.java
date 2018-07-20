package com.project.udacity.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.project.udacity.bakingapp.Ingredient;
import com.project.udacity.bakingapp.R;
import com.project.udacity.bakingapp.Recipe;

import java.util.List;

public class WidgetProvider extends AppWidgetProvider {

    SharedPreferences sharedPreferences;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String recipeName, List<Ingredient> ingredientList) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.recipeNameText, recipeName);
        views.removeAllViews(R.id.ingredientsContainer);
        for (Ingredient ingredient : ingredientList) {
            RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
                    R.layout.ingredient_widget_item);
            ingredientView.setTextViewText(R.id.txt_quantity, String.valueOf(ingredient.getQuantity()));
            ingredientView.setTextViewText(R.id.txt_measure, String.valueOf(ingredient.getMeasure()));
            ingredientView.setTextViewText(R.id.txt_ingredient, String.valueOf(ingredient.getIngredient()));
            views.addView(R.id.ingredientsContainer, ingredientView);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String result = sharedPreferences.getString("widgetRecipe", null);
        if (result != null) {

            Gson gson = new Gson();
            Recipe recipe = gson.fromJson(result, Recipe.class);
            String recipeName = recipe.getName();

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, recipeName, recipe.getIngredients());
            }
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}