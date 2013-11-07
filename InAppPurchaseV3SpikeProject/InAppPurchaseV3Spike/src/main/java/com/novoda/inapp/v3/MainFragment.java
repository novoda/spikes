package com.novoda.inapp.v3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.util.*;

public class MainFragment extends Fragment {

    private static final String TAG = "IABEX";
    private IabHelper iabHelper;
    private boolean purchasesAvailableOnThisDevice = false;

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
                logError(result);
                return;
            }
            purchasesAvailableOnThisDevice = true;
            iabHelper.queryInventoryAsync(queryInventoryFinishedListener);
        }
    };

    private void logError(IabResult result) {
        Log.e(TAG, "Error setting up IAB");
        int responseCode = result.getResponse();
        switch (responseCode) {
            case IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                Log.e(TAG, "IAB not available on this device");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                Log.e(TAG, "IAB Developer Error");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_ERROR:
                Log.e(TAG, "IAB Error");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                Log.e(TAG, "IAB Item already owned error");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
                Log.e(TAG, "IAB item not owned error");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                Log.e(TAG, "IAB item unavailable");
                break;
            case IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED:
                Log.e(TAG, "IAB user cancelled");
                break;
            default:
                Log.e(TAG, "IAB Uncaught error code: " + responseCode);
                break;
        }
    }

    private IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure()) {
                logError(result);
                return;
            }
            for (Purchase purchase : inv.getAllPurchases()) {
                String sku = purchase.getSku();
                SkuDetails skuDetails = inv.getSkuDetails(sku);
                Log.d(TAG, "Owned item : " + skuDetails.getTitle());
                Log.d(TAG, "and it cost : " + skuDetails.getPrice());
            }
            // If you have the SKU of an un-owned item, you can also query for price etc
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button purchaseFiveButton = (Button) rootView.findViewById(R.id.fragment_main_button_purchase_five_coins);
        purchaseFiveButton.setOnClickListener(onFiveButtonClicked);
        Button purchaseTenButton = (Button) rootView.findViewById(R.id.fragment_main_button_purchase_ten_coins);

        return rootView;
    }

    private View.OnClickListener onFiveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!purchasesAvailableOnThisDevice) {
                Toast.makeText(v.getContext(), "IAB not available on this device", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(v.getContext(), "Starting payment process", Toast.LENGTH_SHORT).show();



        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        iabHelper.dispose();
    }
}
