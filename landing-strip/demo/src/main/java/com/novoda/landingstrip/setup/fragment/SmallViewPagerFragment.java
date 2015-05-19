package com.novoda.landingstrip.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novoda.landing_strip.R;
import com.novoda.landingstrip.setup.SmallData;

public class SmallViewPagerFragment extends Fragment {

    private static final String DATA_KEY = "key";

    public static Fragment newInstance(SmallData data) {
        SmallViewPagerFragment viewPagerFragment = new SmallViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_KEY, data);
        viewPagerFragment.setArguments(bundle);
        return viewPagerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_demo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view).setText(getData().getContent());
    }

    private SmallData getData() {
        return (SmallData) getArguments().getSerializable(DATA_KEY);
    }
}
