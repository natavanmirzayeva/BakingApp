package com.project.udacity.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehseti on 6.5.2018.
 */

public class Recipe implements Parcelable
{
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("ingredients")
    public List<Ingredient> ingredients;
    @SerializedName("steps")
    public List<Step> steps;
    @SerializedName("servings")
    public int servings;
    @SerializedName("image")
    public String image;


    public Recipe(Parcel in)
    {
        this.id = in.readInt();
        this.name = in.readString();
        ingredients = new ArrayList<Ingredient>();
        in.readList(ingredients,Recipe.class.getClassLoader());
        steps = new ArrayList<Step>();
        in.readList(steps,Recipe.class.getClassLoader());
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public Recipe() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    public static final Creator CREATOR  = new Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
