package com.project.udacity.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mehseti on 6.5.2018.
 */

public class Step implements Parcelable
{
    @SerializedName("id")
    public int id;
    @SerializedName("shortDescription")
    public String shortDescription;
    @SerializedName("description")
    public String description;
    @SerializedName("videoURL")
    public String videoURL;
    @SerializedName("thumbnailURL")
    public String thumbnailURL;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public Step(Parcel in)
    {
        this.id = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }

    public static final Creator CREATOR  = new Creator<Step>() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
