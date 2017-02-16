/**
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0
 * <p>
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.cognito.sync.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.cognito.sync.devauth.client.SharedPreferencesWrapper;
import com.amazonaws.cognito.sync.devauth.client.Response;
import com.amazonaws.cognito.sync.devauth.client.ServerApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A class which performs the task of authentication the user. For the sample it
 * validates a set of username and possword against the sample Cognito developer
 * authentication application
 */
public class FirebaseTokenTask extends
        AsyncTask<String, Void, String> {

    public static final String TAG = "FirebaseAuthentication";
    private boolean isSuccessful;

    private final Context context;
    private final ServerApiClient apiClient;

    public FirebaseTokenTask(Context context, ServerApiClient apiClient) {
        this.context = context;
        this.apiClient = apiClient;
    }

    @Override
    protected String doInBackground(String... params) {
        Response response = apiClient.getFirebaseToken(null);
        isSuccessful = response.requestWasSuccessful();
        return response.getResponseMessage();
    }

    @Override
    protected void onPostExecute(final String result) {
        if (!isSuccessful) {
            new AlertDialog.Builder(context).setTitle("Login error")
                    .setMessage("Error trying to login to Firebase").show();
        } else {
            Log.i(TAG, "requesting sign-in for token: "+ result + " *");
            FirebaseAuth.getInstance().signInWithCustomToken(result.trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCustomToken failed!", task.getException());
                                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferencesWrapper.registerFirebaseToken(PreferenceManager.getDefaultSharedPreferences(context), result);
                                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
