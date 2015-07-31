package com.example.larkinmcmahon.geogoals;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by connor on 7/28/15.
 */
public class Goal implements Parcelable {
    private List<LatLng> mLocations;
    private List<Integer> mRadii;
    private String mTitle;

    public Goal(String title, List<LatLng> locations, List<Integer> radii) {
        mTitle = title;
        mLocations = locations;
        mRadii = radii;
    }

    public Goal(String title){
        mTitle = title;
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
    }

    private Goal(Parcel in){
        mLocations = new ArrayList<LatLng>();
        mRadii = new ArrayList<Integer>();
        mTitle = in.readString();
        in.readTypedList(mLocations, LatLng.CREATOR);
        in.readList(mRadii, Integer.class.getClassLoader() );
    }

    public static final Parcelable.Creator<Goal> CREATOR
            = new Parcelable.Creator<Goal>() {
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        public Goal[] newArray(int size){
            return new Goal[size];
        }
    };

    public void addGeofence(LatLng coord, int radius){
        mLocations.add(coord);
        mRadii.add(radius);
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public String getTitle(){
        return mTitle;
    }

    public List<LatLng> getLocations(){
        return mLocations;
    }

    public List<Integer> getRadii(){
        return mRadii;
    }

    //unsure what to put here
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeString(mTitle);
        out.writeTypedList(mLocations);
        out.writeList(mRadii);
    }

}
