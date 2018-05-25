package com.indracompany.sofia2.android.healthcheckapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mbriceno on 18/05/2018.
 */

public class MainItem implements Parcelable {

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int imageId;
    private String description;

    public MainItem(){

    }

    private MainItem(Parcel in){
        imageId = in.readInt();
        description = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageId);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<MainItem> CREATOR
            = new Parcelable.Creator<MainItem>() {

        @Override
        public MainItem createFromParcel(Parcel in) {
            return new MainItem(in);
        }

        @Override
        public MainItem[] newArray(int size) {
            return new MainItem[size];
        }
    };
}

