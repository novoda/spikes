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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.cognito.sync.devauth.client.LoginResponse;
import com.amazonaws.cognito.sync.devauth.client.Response;
import com.amazonaws.cognito.sync.devauth.client.ServerApiClient;

/**
 * A class which performs the task of authentication the user. For the sample it
 * validates a set of username and possword against the sample Cognito developer
 * authentication application
 */
public class ServerLoginTask extends AsyncTask<LoginCredentials, Void, Boolean> {

    private final Context context;
    private final ServerApiClient apiClient;

    public ServerLoginTask(Context context, ServerApiClient apiClient, SharedPreferences preferences) {
        this.context = context;
        this.apiClient = apiClient;
    }

    @Override
    protected Boolean doInBackground(LoginCredentials... params) {
        String userName = params[0].getUsername();
        Response response = apiClient.login(userName, params[0].getPassword());
        if (response.requestWasSuccessful()) {
            String deviceKey = ((LoginResponse) response).getKey();
            Cognito.INSTANCE.getIdentifiers().registerUser(userName, deviceKey);
        }
        return response.requestWasSuccessful();
    }

    @Override
    protected void onPostExecute(Boolean isSuccessful) {
        if (isSuccessful) {
            Toast.makeText(context, "Logged in!", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(context).setTitle("Login error")
                    .setMessage("Failed to log in!").show();
        }
    }
}
