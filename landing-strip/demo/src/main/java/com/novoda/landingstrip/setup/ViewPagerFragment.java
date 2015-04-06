package com.novoda.landingstrip.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewPagerFragment extends Fragment {

    private static final String DATA_KEY = "key";

    public static Fragment newInstance(Data data) {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_KEY, data);
        viewPagerFragment.setArguments(bundle);
        return viewPagerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.novoda.landing_strip.R.layout.fragment_demo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view).setText(getData().getContent());
    }

    private Data getData() {
        return (Data) getArguments().getSerializable(DATA_KEY);
    }
}
