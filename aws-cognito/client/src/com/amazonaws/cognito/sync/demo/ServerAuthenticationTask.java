/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.cognito.sync.devauth.client.Response;
import com.amazonaws.cognito.sync.devauth.client.ServerApiClient;

/**
 * A class which performs the task of authentication the user. For the sample it
 * validates a set of username and possword against the sample Cognito developer
 * authentication application
 */
public class ServerAuthenticationTask extends
        AsyncTask<LoginCredentials, Void, Void> {

    // The user name or the developer user identifier you will pass to the
    // Amazon Cognito in the GetOpenIdTokenForDeveloperIdentity API
    private String userName;

    private boolean isSuccessful;

    private final Context context;
    private final ServerApiClient apiClient;

    public ServerAuthenticationTask(Context context, ServerApiClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
    }

    @Override
    protected Void doInBackground(LoginCredentials... params) {

        Response response = apiClient.login(params[0].getUsername(), params[0].getPassword());
        isSuccessful = response.requestWasSuccessful();
        userName = params[0].getUsername();

        if (isSuccessful) {
            Cognito.addLogins(
                ((ServerCognitoIdentityProvider) Cognito.getCredentialsProvider()
                                    .getIdentityProvider()).getProviderName(),
                            userName);
            // Always remember to call refresh after updating the logins map
            Cognito.getCredentialsProvider().getIdentityProvider().refresh();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (!isSuccessful) {
            new AlertDialog.Builder(context).setTitle("Login error")
                    .setMessage("Configuration error or username and password do not match!!").show();
        } else {
            Toast.makeText(context, "Logged in to Cognito!", Toast.LENGTH_SHORT).show();
        }
    }
}
