package com.gertherb.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;

public abstract class BetterAuthenticatorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (callerHasAccountManagerCallback()) {
            getAuthenticatorResponseFromExtras().onRequestContinued();
        }
    }

    protected void setSuccessResponse(Token token) {
        addAccount(token);
        if (callerHasAccountManagerCallback()) {
            Bundle bundle = createResponseBundle(token);
            setResponseResult(bundle);
        }
    }

    private void addAccount(Token token) {
        AccountManager accountManager = AccountManager.get(this.getApplicationContext());
        Account account = new Account(token.getUserName(), getAccountName());
        //Todo : save password or refresh token here to enable refresh of authToken
        accountManager.addAccountExplicitly(account, token.getAuthToken(), null);
        accountManager.setAuthToken(account, getAccountType(), token.getAuthToken());
    }

    protected void setErrorResultAuthenticator(int errorCode, Throwable throwable) {
        if (callerHasAccountManagerCallback()) {
            setResponseError(errorCode, throwable.getLocalizedMessage());
        }
    }

    protected void setCancelResultAuthenticator(Throwable throwable) {
        if (callerHasAccountManagerCallback()) {
            setResponseError(AccountManager.ERROR_CODE_CANCELED, throwable.getLocalizedMessage());
        }
    }

    private Bundle createResponseBundle(Token result) {
        Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, getAccountName());
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, getAccountType());
        bundle.putString(AccountManager.KEY_AUTHTOKEN, result.getAuthToken());
        return bundle;
    }

    private void setResponseResult(Bundle result) {
        AccountAuthenticatorResponse response = getAuthenticatorResponseFromExtras();
        response.onResult(result);
        setResult(RESULT_OK);
        finish();
    }

    private void setResponseError(int errorCode, String message) {
        AccountAuthenticatorResponse response = getAuthenticatorResponseFromExtras();
        response.onError(errorCode, message);
        finish();
    }

    private boolean callerHasAccountManagerCallback() {
        return getIntent().hasExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
    }

    private AccountAuthenticatorResponse getAuthenticatorResponseFromExtras() {
        return getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
    }

    private String getAccountName() {
        return getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
    }

    private String getAccountType() {
        return getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
    }

}
