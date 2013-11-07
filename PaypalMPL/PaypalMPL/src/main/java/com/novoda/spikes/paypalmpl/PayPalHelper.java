package com.novoda.spikes.paypalmpl;

import android.content.Context;
import android.content.Intent;

import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPreapproval;

public class PayPalHelper {
    private final Context context;
    private static final String PAYPAL_APP_ID = "APP_ID";
    private static final String PAYPAL_LANGUAGE = "en_US";

    public PayPalHelper(Context context) {

        this.context = context;
    }

    public void init() {
        PayPal payPal = PayPal.getInstance();
        if (payPal == null) {
            payPal = PayPal.initWithAppID(context, PAYPAL_APP_ID, PayPal.ENV_NONE);
            payPal.setLanguage(PAYPAL_LANGUAGE);
        }
    }

    public PayPalPreapproval createPreapproval(String preapprovalKey) {
        PayPal.getInstance().setPreapprovalKey(preapprovalKey);

        PayPalPreapproval preapproval = new PayPalPreapproval();
        preapproval.setCurrencyType("USD");
        preapproval.setIpnUrl("http://www.exampleapp.com/ipn");
        preapproval.setMemo("Why hello, and welcome to the preapproval memo.");
        preapproval.setMerchantName("Joe's Bear Emporium");
        return preapproval;
    }

    public Intent getPreapprovalIntent(String preapprovalKey) {
        PayPalPreapproval preapproval = createPreapproval(preapprovalKey);
        return PayPal.getInstance().preapprove(preapproval, context);
    }
}
