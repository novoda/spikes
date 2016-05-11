package com.novoda.bonfire.navigation;

public interface LoginNavigator extends Navigator {

    void toGooglePlusLogin();

    void attach(LoginResultListener loginResultListener);

    void detach(LoginResultListener loginResultListener);

    public interface LoginResultListener {

        public void onGooglePlusLoginSuccess(String tokenId);

        public void onGooglePlusLoginFailed(String statusMessage);

    }

}
