package com.novoda.viewpageradapter;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapterState implements Parcelable {

    public static final Creator<ViewPagerAdapterState> CREATOR = new Creator<ViewPagerAdapterState>() {

        public ViewPagerAdapterState createFromParcel(Parcel in) {
            return ViewPagerAdapterState.from(in);
        }

        public ViewPagerAdapterState[] newArray(int size) {
            return new ViewPagerAdapterState[size];
        }

    };

    private final Map<Integer, SparseArray<Parcelable>> viewStates;

    public static ViewPagerAdapterState newInstance() {
        return new ViewPagerAdapterState(new HashMap<Integer, SparseArray<Parcelable>>());
    }

    private static ViewPagerAdapterState from(Parcel in) {
        Bundle bundle = in.readBundle();
        Map<Integer, SparseArray<Parcelable>> viewStates = new HashMap<>(bundle.keySet().size());
        for (String key : bundle.keySet()) {
            SparseArray<Parcelable> sparseParcelableArray = bundle.getSparseParcelableArray(key);
            viewStates.put(Integer.parseInt(key), sparseParcelableArray);
        }
        return new ViewPagerAdapterState(viewStates);
    }

    private ViewPagerAdapterState(Map<Integer, SparseArray<Parcelable>> viewStates) {
        this.viewStates = viewStates;
    }

    public void put(int position, SparseArray<Parcelable> viewState) {
        viewStates.put(position, viewState);
    }

    public SparseArray<Parcelable> get(int position) {
        return viewStates.get(position);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        for (Map.Entry<Integer, SparseArray<Parcelable>> entry : viewStates.entrySet()) {
            bundle.putSparseParcelableArray(String.valueOf(entry.getKey()), entry.getValue());
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
