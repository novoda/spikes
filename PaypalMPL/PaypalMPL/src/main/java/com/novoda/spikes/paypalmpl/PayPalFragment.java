package com.novoda.spikes.paypalmpl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;

public class PayPalFragment extends Fragment {

    private static final String PREAPPROVAL_KEY = "PREAPPROVAL_KEY";
    private static final int PREAPPROVAL_REQUEST_CODE = 0;

    private PayPalHelper payPalHelper;

    public PayPalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        payPalHelper = new PayPalHelper(getActivity());
        payPalHelper.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);
        setupView(rootView);
        return rootView;
    }

    private void setupView(ViewGroup rootView) {
        PayPal payPal = PayPal.getInstance();
        CheckoutButton preapprovalButton = payPal.getCheckoutButton(getActivity(), PayPal.BUTTON_194x37, CheckoutButton.TEXT_PAY);
        preapprovalButton.setOnClickListener(preapprovalClickListener);
        rootView.addView(preapprovalButton);
    }

    private View.OnClickListener preapprovalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent preapprovalIntent = payPalHelper.getPreapprovalIntent(PREAPPROVAL_KEY);
            startActivityForResult(preapprovalIntent, PREAPPROVAL_REQUEST_CODE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PREAPPROVAL_REQUEST_CODE) {
            return;
        }

        String resultTitle = "";
        String resultInfo = "";
        String resultExtra = "";
        switch (resultCode) {
            case Activity.RESULT_OK:
                resultTitle = "SUCCESS";
                resultInfo = "You have successfully completed this preapproval.";
                resultExtra = "Transaction ID: " + data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                break;
            case Activity.RESULT_CANCELED:
                resultTitle = "CANCELED";
                resultInfo = "The transaction has been cancelled.";
                resultExtra = "";
                break;
            case PayPalActivity.RESULT_FAILURE:
                resultTitle = "FAILURE";
                resultInfo = data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                resultExtra = "Error ID: " + data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
        }

        Log.v("PayPalFragment", resultTitle + " " + resultInfo + " " + resultExtra);
        Toast.makeText(getActivity(), resultTitle + " " + resultInfo, Toast.LENGTH_SHORT).show();
    }
}