package com.novoda.inapp.v3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;

public class MainFragment extends Fragment {

    private IabHelper iabHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iabHelper = new IabHelper(getActivity(), "todoGetBase64PublicKeyFromGooglePlayStore");
        iabHelper.startSetup(onIabSetupFinished);
    }

    private IabHelper.OnIabSetupFinishedListener onIabSetupFinished = new IabHelper.OnIabSetupFinishedListener() {
        @Override
        public void onIabSetupFinished(IabResult result) {
            if (result.isFailure()) {
                Log.e("TAG", "Error setting up IAB");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iabHelper.dispose();
    }
}
