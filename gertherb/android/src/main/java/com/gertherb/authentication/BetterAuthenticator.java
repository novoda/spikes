package com.gertherb.authentication;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class BetterAuthenticator extends AbstractAccountAuthenticator {

    private final AccountManager accountManager;
    private final Context context;

    public BetterAuthenticator(Context context) {
        super(context);
        this.context = context;
        this.accountManager = AccountManager.get(context.getApplicationContext());
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        options = (options == null) ? new Bundle() : options;
        Bundle result = new Bundle();
        Intent activity = createLoginIntent(context, accountType, authTokenType, requiredFeatures, options);
        activity.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountType);
        activity.putExtra(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
        activity.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        activity.putExtras(options);
        result.putParcelable(AccountManager.KEY_INTENT, activity);
        return result;
    }

    protected abstract Intent createLoginIntent(Context context, String accountType, String authTokenType, String[] requiredFeatures, Bundle options);

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        //Todo: ensure scope here
        Bundle bundle = new Bundle();
        if (accountExists(account.type)) {
            String token = accountManager.peekAuthToken(account, authTokenType);
            return passBackCurrentAuthToken(account.name, authTokenType, bundle, token);
        }

        return promptToLogin(response, account, authTokenType, options, bundle);
    }

    private boolean accountExists(String authTokenType) {
        return getAccount(authTokenType) != null;
    }

    private Account getAccount(String authTokenType) {
        Account[] accounts = accountManager.getAccountsByType(authTokenType);
        return accounts.length > 0 ? accounts[0] : null;
    }

    private Bundle passBackCurrentAuthToken(String accountName, String authTokenType, Bundle bundle, String token) {
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
        bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
        return bundle;
    }

    private Bundle promptToLogin(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options, Bundle bundle) {
        //Todo: should we pass the proper supported features ?
        bundle.putParcelable(AccountManager.KEY_INTENT, createLoginIntent(context, account.type, authTokenType, null, options));
        bundle.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
