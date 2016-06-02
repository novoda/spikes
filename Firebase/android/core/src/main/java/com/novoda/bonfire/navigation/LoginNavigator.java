package com.novoda.bonfire.navigation;

public interface LoginNavigator extends Navigator {

    void toGooglePlusLogin();

    void attach(LoginResultListener loginResultListener);

    void detach(LoginResultListener loginResultListener);

    interface LoginResultListener {

        void onGooglePlusLoginSuccess(String tokenId);

        void onGooglePlusLoginFailed(String statusMessage);

    }

}
