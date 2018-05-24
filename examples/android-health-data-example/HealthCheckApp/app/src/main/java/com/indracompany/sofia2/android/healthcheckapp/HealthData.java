package com.indracompany.sofia2.android.healthcheckapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mbriceno on 17/05/2018.
 */

public class HealthData implements Parcelable{

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getSysPressure() {
        return sysPressure;
    }

    public void setSysPressure(int sysPressure) {
        this.sysPressure = sysPressure;
    }

    public int getDiaPressure() {
        return diaPressure;
    }

    public void setDiaPressure(int diaPressure) {
        this.diaPressure = diaPressure;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String comments;
    private int height;
    private int weight;
    private int sysPressure;
    private int diaPressure;
    private String timestamp;


    public HealthData(){

    }

    private HealthData(Parcel in){
        comments = in.readString();
        height = in.readInt();
        weight = in.readInt();
        sysPressure = in.readInt();
        diaPressure = in.readInt();
        timestamp = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comments);
        parcel.writeInt(height);
        parcel.writeInt(weight);
        parcel.writeInt(sysPressure);
        parcel.writeInt(diaPressure);
        parcel.writeString(timestamp);
    }

    public static final Parcelable.Creator<HealthData> CREATOR
            = new Parcelable.Creator<HealthData>() {

        @Override
        public HealthData createFromParcel(Parcel in) {
            return new HealthData(in);
        }

        @Override
        public HealthData[] newArray(int size) {
            return new HealthData[size];
        }
    };
}
