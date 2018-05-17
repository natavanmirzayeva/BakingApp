package com.project.udacity.bakingapp;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mehseti on 6.5.2018.
 */

public class Ingredient
{
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("measure")
    public String measure;
    @SerializedName("ingredient")
    public String ingredient;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

}
